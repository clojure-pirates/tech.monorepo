(ns tech.lib.svm-test
  (:require [clojure.test :refer :all]
            [tech.lib.svm :as svm]
            [tech.ml.verify.regression :as verify-regression]
            [tech.ml.verify.classification :as verify-classification]))


(deftest basic-regression
  (verify-regression/scaled-features {:model-type :libsvm/regression}))


(deftest regression-gridsearch
  (verify-regression/auto-gridsearch-simple {:model-type :libsvm/regression}))


(deftest basic-classification
  (verify-classification/classify-fruit {:model-type :libsvm/classification}))


(deftest basic-classification-gridsearch
  (verify-classification/auto-gridsearch-fruit {:model-type :libsvm/classification}))
