(ns tech.lib.smile.classification-test
  (:require [tech.ml.verify.classification :as verify-cls]
            [tech.lib.smile.classification]
            [tech.ml :as ml]
            [clojure.test :refer :all]))


(deftest ada-boost
  (verify-cls/classify-fruit {:model-type :smile.classification/ada-boost}))


(deftest ada-boost-gridsearch
  (verify-cls/auto-gridsearch-fruit {:model-type :smile.classification/ada-boost}))


(deftest fld
  (verify-cls/classify-fruit {:model-type :smile.classification/fld}))


(deftest knn
  (verify-cls/classify-fruit {:model-type :smile.classification/knn}))


(deftest knn-gridsearch
  (verify-cls/auto-gridsearch-fruit {:model-type :smile.classification/knn}))


;; This one is all over the board.  There are a few hyperparameters
;; that probably need to be tuned.
(deftest svm
  (verify-cls/classify-fruit {:model-type :smile.classification/svm}))


(deftest svm-gridsearch
  (verify-cls/auto-gridsearch-fruit {:model-type :smile.classification/svm}))


(deftest svm-binary
  (testing "binary classifiers do not throw"
    (let [model (ml/train {:model-type :smile.classification/svm}
                          :features :label
                          (concat (repeat 10 {:features 1 :label :a})
                                  (repeat 10 {:features -1 :label :b})))]
      (is (not (nil? model))))))


(deftest lda
  (verify-cls/classify-fruit {:model-type :smile.classification/linear-discriminant-analysis}))


(deftest lda-gridsearch
  (verify-cls/auto-gridsearch-fruit {:model-type :smile.classification/linear-discriminant-analysis}))


;;QDA won't work on the fruit example, not sure why at this point.
;; (deftest qda
;;   (verify-cls/classify-fruit
;;    :smile.classification {:model-type :quadratic-discriminant-analysis}))


;; (deftest qda-gridsearch
;;   (verify-cls/auto-gridsearch-fruit
;;    :smile.classification {:model-type :quadratic-discriminant-analysis}))


(deftest rda
  (verify-cls/classify-fruit {:model-type :smile.classification/regularized-discriminant-analysis}))


(deftest rda-gridsearch
  (verify-cls/auto-gridsearch-fruit {:model-type :smile.classification/regularized-discriminant-analysis}))


(deftest logistic-regression
  (verify-cls/classify-fruit {:model-type :smile.classification/logistic-regression}))


(deftest logistic-regression-gridsearch
  (verify-cls/auto-gridsearch-fruit {:model-type :smile.classification/logistic-regression}))


(deftest naive-bayes
  (verify-cls/classify-fruit {:model-type :smile.classification/naive-bayes}))


(deftest naive-bayes-gridsearch
  (verify-cls/auto-gridsearch-fruit {:model-type :smile.classification/naive-bayes}))
