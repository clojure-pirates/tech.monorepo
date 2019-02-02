(ns tech.lib.tablesaw.datatype
  "Bindings so that you can do math on tablesaw columns."
  (:require [tech.datatype.base :as base]
            [tech.datatype.java-primitive :as primitive]
            [clojure.core.matrix.protocols :as mp]
            [tech.lib.fastutil.datatype :as dtype-fastutil])
  (:import [tech.tablesaw.api ShortColumn IntColumn LongColumn
            FloatColumn DoubleColumn]
           [it.unimi.dsi.fastutil.shorts ShortArrayList]
           [it.unimi.dsi.fastutil.ints IntArrayList]
           [it.unimi.dsi.fastutil.longs LongArrayList]
           [it.unimi.dsi.fastutil.floats FloatArrayList]
           [it.unimi.dsi.fastutil.doubles DoubleArrayList]
           [java.nio ByteBuffer ShortBuffer IntBuffer LongBuffer
            FloatBuffer DoubleBuffer Buffer]
           [java.lang.reflect Field]))


(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)


(declare make-tablesaw-column)


(defn short-col-cast ^ShortColumn [item] item)
(def short-data-field
  (memoize
   (fn []
     (doto (.getDeclaredField ShortColumn "data")
       (.setAccessible true)))))
(defn short-col-data
  ^ShortArrayList [^ShortColumn col]
  (when-not (instance? ShortColumn col)
    (throw (ex-info "Short data requested on object that is not a short column"
                    {:object-type (type col)})))
  (.get ^Field (short-data-field) col))

(defn int-col-cast ^IntColumn [item] item)
(def int-data-field
  (memoize
   (fn []
     (doto (.getDeclaredField IntColumn "data")
         (.setAccessible true)))))
(defn int-col-data
  ^IntArrayList [^IntColumn col]
  (when-not (instance? IntColumn col)
    (throw (ex-info "Int data requested on object that is not a int column"
                    {:object-type (type col)})))
  (.get ^Field (int-data-field) col))

(defn long-col-cast ^LongColumn [item] item)
(def long-data-field
  (memoize
   (fn []
     (doto (.getDeclaredField LongColumn "data")
         (.setAccessible true)))))
(defn long-col-data
  ^LongArrayList [^LongColumn col]
  (when-not (instance? LongColumn col)
    (throw (ex-info "Long data requested on object that is not a long column"
                    {:object-type (type col)})))
  (.get ^Field (long-data-field) col))

(defn float-col-cast ^FloatColumn [item] item)
(def float-data-field
  (memoize
   (fn []
     (doto (.getDeclaredField FloatColumn "data")
         (.setAccessible true)))))
(defn float-col-data
  ^FloatArrayList [^FloatColumn col]
  (when-not (instance? FloatColumn col)
    (throw (ex-info "Float data requested on object that is not a float column"
                    {:object-type (type col)})))
  (.get ^Field (float-data-field) col))

(defn double-col-cast ^DoubleColumn [item] item)
(def double-data-field
  (memoize
   (fn []
     (doto (.getDeclaredField DoubleColumn "data")
         (.setAccessible true)))))
(defn double-col-data
  ^DoubleArrayList [^DoubleColumn col]
  (when-not (instance? DoubleColumn col)
    (throw (ex-info "Double data requested on object that is not a double column"
                    {:object-type (type col)})))
  (.get ^Field (double-data-field) col))






(defmacro datatype->column-cast-fn
  [datatype item]
  (case datatype
    :int16 `(short-col-cast ~item)
    :int32 `(int-col-cast ~item)
    :int64 `(long-col-cast ~item)
    :float32 `(float-col-cast ~item)
    :float64 `(double-col-cast ~item)))


(defmacro datatype->column-data-cast-fn
  [datatype item]
  (case datatype
    :int16 `(short-col-data ~item)
    :int32 `(int-col-data ~item)
    :int64 `(long-col-data ~item)
    :float32 `(float-col-data ~item)
    :float64 `(double-col-data ~item)))



(defmacro extend-tablesaw-type
  [typename datatype]
  `(clojure.core/extend
       ~typename
     base/PDatatype
     {:get-datatype (fn [arg#] ~datatype)}
     base/PContainerType
     {:container-type (fn [_#] :typed-buffer)}
     base/PAccess
     {:get-value (fn [item# ^long idx#]
                   (.get (datatype->column-cast-fn ~datatype item#) idx#))
      :set-value! (fn [item# ^long offset# value#]
                    (.set (datatype->column-cast-fn ~datatype item#)
                          (int offset#)
                          (primitive/datatype->cast-fn :unused ~datatype value#))
                    item#)
      :set-constant! (fn [item# ^long offset# value# ^long elem-count#]
                       (base/set-constant! (primitive/->buffer-backing-store item#)
                                           offset# value# elem-count#))}

     base/PCopyRawData
     {:copy-raw->item! (fn [raw-data# ary-target# target-offset# options#]
                         (base/copy-raw->item! raw-data# (primitive/->buffer-backing-store ary-target#)
                                               target-offset# options#))}
     base/PPrototype
     {:from-prototype (fn [src-ary# datatype# shape#]
                        (when-not (= 1 (base/shape->ecount shape#))
                          (throw (ex-info "Base containers cannot have complex shapes"
                                          {:shape shape#})))
                        (make-tablesaw-column datatype# (base/shape->ecount shape#)
                                              {:column-name (.name (datatype->column-cast-fn ~datatype src-ary#))}))}

     primitive/PToBuffer
     {:->buffer-backing-store (fn [item#]
                                (when (> (.countMissing (datatype->column-cast-fn ~datatype item#)) 0)
                                  (throw (ex-info "Datatype operations only work on columns with no missing data"
                                                  {:missing-count (.countMissing
                                                                   (datatype->column-cast-fn ~datatype item#))})))
                                (primitive/->buffer-backing-store
                                 (datatype->column-data-cast-fn ~datatype item#)))}

     primitive/POffsetable
     {:offset-item (fn [src-buf# offset#]
                     (primitive/offset-item (primitive/->buffer-backing-store src-buf#)
                                            offset#))}

     primitive/PToArray
     {:->array (fn [item#]
                 (primitive/->array
                  (primitive/->buffer-backing-store item#)))
      :->array-copy (fn [item#]
                      (primitive/->array-copy
                       (primitive/->buffer-backing-store item#)))}

     mp/PElementCount
     {:element-count (fn [item#]
                       (.size (datatype->column-cast-fn ~datatype item#)))}))


(extend-tablesaw-type ShortColumn :int16)
(extend-tablesaw-type IntColumn :int32)
(extend-tablesaw-type LongColumn :int64)
(extend-tablesaw-type FloatColumn :float32)
(extend-tablesaw-type DoubleColumn :float64)


(defn make-tablesaw-column
  ([datatype elem-count-or-seq {:keys [column-name]
                                :or {column-name "_unnamed"}
                                :as options}]
   (let [src-data (primitive/make-array-of-type datatype elem-count-or-seq options)
         ^String column-name column-name]
     (case datatype
       :int16 (ShortColumn/create column-name ^shorts src-data)
       :int32 (IntColumn/create column-name ^ints src-data)
       :int64 (LongColumn/create column-name ^longs src-data)
       :float32 (FloatColumn/create column-name ^floats src-data)
       :float64 (DoubleColumn/create column-name ^doubles src-data))))
  ([datatype elem-count-or-seq]
   (make-tablesaw-column datatype elem-count-or-seq {})))
