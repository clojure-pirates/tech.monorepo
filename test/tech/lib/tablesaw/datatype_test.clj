(ns tech.lib.tablesaw.datatype-test
  (:require [tech.datatype :as dtype]
            [tech.lib.tablesaw.datatype :as dtype-tablesaw]
            [tech.lib.tablesaw.compute]
            [tech.datatype.java-unsigned :as unsigned]
            [tech.compute.cpu.tensor-math]
            [tech.compute.tensor :as ct]
            [tech.compute.tensor.operations :as ct-ops]
            [clojure.test :refer :all]))


(deftest basic-interfaces
  ;;These things have to be typed buffers.
  (is (unsigned/typed-buffer?
       (dtype-tablesaw/make-tablesaw-column :float32 5))))


(deftest base-datatype-operations
  (let [base-buffer (dtype-tablesaw/make-tablesaw-column :int32 (range 10))
        double-data (dtype/copy! base-buffer (double-array 10))
        byte-data (byte-array (reverse (range 10)))]
    (dtype/copy! byte-data base-buffer)
    (is (= (vec (map double (range 10))))
        (dtype/->vector double-data))
    (is (= (vec (map int (reverse (range 10))))
           (dtype/->vector base-buffer)))))


(deftest some-basic-math
  (let [math-data (-> (dtype-tablesaw/make-tablesaw-column :int16 (range 10))
                      (ct-ops/* 2.0)
                      (ct-ops/+ 4))]
    (is (= [4 6 8 10 12 14 16 18 20 22]
           (dtype/->vector math-data)))))
