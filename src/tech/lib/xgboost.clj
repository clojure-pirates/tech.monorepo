(ns tech.lib.xgboost
  (:require [tech.datatype :as dtype]
            [tech.core.parallel :as parallel]
            [tech.ml.dataset :as dataset]
            [tech.ml.model :as model]
            [tech.ml.protocols :as ml-proto]
            [tech.ml.registry :as ml-registry]
            [tech.ml.details :as ml-details]
            [tech.ml :as ml]
            [tech.ml.gridsearch :as ml-gs]
            [clojure.string :as s])
  (:refer-clojure :exclude [load])
  (:import [ml.dmlc.xgboost4j.java Booster XGBoost XGBoostError DMatrix]
           [ml.dmlc.xgboost4j LabeledPoint]
           [java.util Iterator]
           [java.util UUID]
           [java.io ByteArrayInputStream ByteArrayOutputStream]))


(set! *warn-on-reflection* true)


(defmulti model-type->xgboost-objective
  (fn [model-type]
    model-type))


(def objective-types
  {:linear-regression "reg:linear"
   :logistic-regression "reg:logistic"
   ;;logistic regression for binary classification
   :logistic-binary-classification "binary:logistic"
   ;; logistic regression for binary classification, output score before logistic
   ;; transformation
   :logistic-binary-raw-classification "binary:logitraw"
    ;;hinge loss for binary classification. This makes predictions of 0 or 1, rather
    ;;than producing probabilities.
   :binary-hinge-loss "binary:hinge"
   ;; versions of the corresponding objective functions evaluated on the GPU; note that
   ;; like the GPU histogram algorithm, they can only be used when the entire training
   ;; session uses the same dataset
   :gpu-linear-regression "gpu:reg:linear"
   :gpu-logistic-regression "gpu:reg:logistic"
   :gpu-binary-logistic-classification "gpu:binary:logistic"
   :gpu-binary-logistic-raw-classification "gpu:binary:logitraw"

   ;; poisson regression for count data, output mean of poisson distribution
   ;; max_delta_step is set to 0.7 by default in poisson regression (used to safeguard
   ;; optimization)
   :count-poisson "count:poisson"

   ;; Cox regression for right censored survival time data (negative values are
   ;; considered right censored). Note that predictions are returned on the hazard ratio
   ;; scale (i.e., as HR = exp(marginal_prediction) in the proportional hazard function
   ;; h(t) = h0(t) * HR).
   :survival-cox "survival:cox"
   ;; set XGBoost to do multiclass classification using the softmax objective, you also
   ;; need to set num_class(number of classes)
   :multiclass-softmax "multi:softmax"
   ;; same as softmax, but output a vector of ndata * nclass, which can be further
   ;; reshaped to ndata * nclass matrix. The result contains predicted probability of
   ;; each data point belonging to each class.
   :multiclass-softprob "multi:softprob"
   ;; Use LambdaMART to perform pairwise ranking where the pairwise loss is minimized
   :rank-pairwise "rank:pairwise"
   ;; Use LambdaMART to perform list-wise ranking where Normalized Discounted Cumulative
   ;; Gain (NDCG) is maximized
   :rank-ndcg "rank:ndcg"
   ;; Use LambdaMART to perform list-wise ranking where Mean Average Precision (MAP) is
   ;; maximized
   :rank-map "rank:map"
   ;; gamma regression with log-link. Output is a mean of gamma distribution. It might
   ;; be useful, e.g., for modeling insurance claims severity, or for any outcome that
   ;; might be gamma-distributed.
   :gamma-regression "reg:gamma"
    ;; Tweedie regression with log-link. It might be useful, e.g., for modeling total
    ;; loss in insurance, or for any outcome that might be Tweedie-distributed.
   :tweedie-regression "reg:tweedie"})


(defmethod model-type->xgboost-objective :default
  [model-type]
  (if-let [retval (get objective-types model-type)]
    retval
    (throw (ex-info "Unrecognized xgboost model type"
                    {:model-type model-type
                     :possible-types (keys objective-types)}))))


(defmethod model-type->xgboost-objective :regression
  [_]
  (model-type->xgboost-objective :linear-regression))


(defmethod model-type->xgboost-objective :binary-classification
  [_]
  (model-type->xgboost-objective :logistic-binary-classification))


(defmethod model-type->xgboost-objective :classification
  [_]
  (model-type->xgboost-objective :multiclass-softprob))


;; For a full list of options, check out:
;; https://xgboost.readthedocs.io/en/latest/parameter.html

(defn load
  ^Booster [^String path]
  (XGBoost/loadModel path))


(defn save
  [^Booster trained-model ^String path]
  (.saveModel trained-model path))


(defn model->byte-array
  ^bytes [^Booster model]
  (model/model->byte-array model))


(defn byte-array->model
  ^Booster [^bytes data]
  (model/byte-array->model data))


(defn- ->data
  [item default-value]
  (if item
    (dtype/get-value item 0)
    default-value))


(defn dataset->labeled-point-iterator
  "Create an iterator to labeled points from a possibly quite large
  sequence of maps.  Sets expected length to length of first entry"
  ^Iterator [dataset & {:keys [weight group]
                        :or {weight 1.0
                             group -1}}]
  (->> dataset
       ;;dataset is now coalesced into float arrays for the values
       ;;and a single float for the label (if it exists).
       (map (fn [{:keys [::dataset/features ::dataset/label]}]
              (LabeledPoint. (float (->data label 0.0)) nil ^floats features)))
       (dataset/sequence->iterator)))


(defn dataset->dmatrix
  "Dataset is a sequence of maps.  Each contains a feature key.
  Returns a dmatrix."
  ^DMatrix [dataset feature-keys label-key]
  (DMatrix. (dataset->labeled-point-iterator dataset feature-keys label-key)
            nil))


(defn- get-objective
  [options]
  (model-type->xgboost-objective
   (or (model/options->model-type options)
       :linear-regression)))

(defn multiclass-objective?
  [objective]
  (or (= objective "multi:softmax")
      (= objective "multi:softprob")))


(defrecord XGBoostSystem []
  ml-proto/PMLSystem
  (system-name [_] :xgboost)
  (coalesce-options [_ _]
    {:datatype :float32
     :container-fn dtype/make-array-of-type
     ;;Start the multiclass label from 0.
     :multiclass-label-base-index 0})
  (gridsearch-options [system options]
    ;;These are just a very small set of possible options:
    ;;https://xgboost.readthedocs.io/en/latest/parameter.html
    {:subsample (ml-gs/linear [0.1 1.0])
     :scale-pos-weight (ml-gs/linear [0.1 2.0])
     :max-depth (comp long (ml-gs/linear-long [2 500]))
     :lambda (ml-gs/linear [0 5])
     :gamma (ml-gs/exp [0.0001 5])
     :eta (ml-gs/linear [0 1])
     :alpha (ml-gs/exp [0.0001 5])})
  (train [_ options coalesced-dataset]
    (let [objective (get-objective options)
          train-dmat (dataset->dmatrix coalesced-dataset ::dataset/features ::dataset/label)
          watches (:watches options)
          round (or (:round options) 25)
          label-map (when (multiclass-objective? objective)
                      (ml-details/options->label-map options (::dataset/label-keys options)))
          params (->> (-> (dissoc options :model-type :watches)
                          (assoc :objective objective))
                      ;;Adding in some defaults
                      (merge  {:alpha 0.0
                               :eta 0.3
                               :lambda 0.0
                               :max-depth 6
                               :scale-pos-weight 1.0
                               :subsample 0.87
                               :silent 1
                               } options
                              (when label-map
                                {:num-class (count label-map)}))
                      (map (fn [[k v]]
                             [(s/replace (name k) "-" "_" ) v]))
                      (into {}))
          ^Booster model (XGBoost/train train-dmat params
                                        (long round)
                                        (or watches {}) nil nil)
          out-s (ByteArrayOutputStream.)]
      (.saveModel model out-s)
      (.toByteArray out-s)))

  (predict [_ options model coalesced-dataset]
    (let [model (XGBoost/loadModel (ByteArrayInputStream. model))
          retval (->> (dataset->dmatrix coalesced-dataset ::dataset/features nil)
                      (.predict model))]
      (if (= "multi:softprob" (get-objective options))
        (let [label-map (ml-details/options->label-map options (::dataset/label-keys options))
              ordered-labels (->> label-map
                                  (sort-by second)
                                  (mapv first))
              label-maps
              (->> retval
                   (map (fn [output-vec]
                          (zipmap ordered-labels output-vec))))]
          label-maps)
        (map first retval)))))


(def system
  (memoize
   (fn []
     (->XGBoostSystem))))


(ml-registry/register-system (system))
