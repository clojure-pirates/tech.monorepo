(ns tech.lib.tvm.gpu-test
  (:require [clojure.test :refer :all]
            [tech.lib.tvm.cpu-test :as cpu-test]
            [tech.lib.tvm.gpu]))


(deftest ^:cuda cuda-basic-add
  (cpu-test/test-add-fn :cuda))


(deftest opencl-basic-add
  (cpu-test/test-add-fn :opencl))
