(ns tech.lib.xgboost-test
  (:require [clojure.test :refer :all]
            [tech.ml.verify.regression :as verify-reg]
            [tech.ml.verify.classification :as verify-cls]
            [tech.lib.xgboost]))


(deftest basic
  (verify-reg/basic-regression {:model-type :xgboost/regression}))


(deftest scaled-features
  (verify-reg/scaled-features {:model-type :xgboost/regression}))


(deftest k-fold-regression
  (verify-reg/k-fold-regression {:model-type :xgboost/regression}))


(deftest regression-gridsearch
  (verify-reg/auto-gridsearch-simple {:model-type :xgboost/regression}))


(deftest classification
  (verify-cls/classify-fruit {:model-type :xgboost/classification}))


(deftest classification-gridsearch
  (verify-cls/auto-gridsearch-fruit {:model-type :xgboost/classification}))
