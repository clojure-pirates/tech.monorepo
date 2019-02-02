(ns tech.lib.tablesaw.compute
  (:require [tech.compute.cpu.typed-buffer :as cpu-typed-buffer]
            ;;Have to have fastutil bound also
            [tech.lib.fastutil.compute])
  (:import [tech.tablesaw.api ShortColumn IntColumn LongColumn
            FloatColumn DoubleColumn]))



(cpu-typed-buffer/generic-extend-java-type ShortColumn)
(cpu-typed-buffer/generic-extend-java-type IntColumn)
(cpu-typed-buffer/generic-extend-java-type LongColumn)
(cpu-typed-buffer/generic-extend-java-type FloatColumn)
(cpu-typed-buffer/generic-extend-java-type DoubleColumn)
