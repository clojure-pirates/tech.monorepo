(ns tech.monorepo
  (:require [hara.lib.jgit :refer [git] :as git]
            [hara.io.file :as fs]))

(def +library-url+ "https://github.com/techascent/%s.git")

(def +library-path+ "original")

(def +library+
  ["tech.datatype"
   "tech.compute"
   "tech.datatype.tablesaw"
   "tech.datatype.fastutil"
   "tech.svm"
   "tech.xgboost"
   "tech.smile"
   "tech.ml-base"
   "tech.opencv"
   "tech.javacpp-datatype"
   "tech.jna"
   "tech.io.aws"
   "tech.io"
   "tech.resource"
   "tech.parallel"
   "tech.queue"
   "tech.mxnet"
   "tvm-clj"])

(def +dir+ ["src" "java" "test"])

(defn tech-url
  [id]
  (format +library-url+ id))

(defn tech-repo
  [id & more]
  (apply str +library-path+ "/" id more))

(defn tech-clone
  ([]
   (println "CLONING TECH")
   (mapv (fn [id]
           (print id "...")
           (tech-clone id)
           (println " DONE")) +library+))
  ([id]
   (if-not (fs/exists? (tech-repo id))
     (git :clone :uri (tech-url id) :directory (tech-repo id)))))

(defn tech-copy
  ([]
   (println "COPYING TECH")
   (doall (for [id  +library+
                dir +dir+]
            (tech-copy id dir))))
  ([id dir]
   (let [source (tech-repo id "/" dir)]
     (fs/copy-into source dir {:include [fs/file?]
                               :options #{:replace-existing}}))))

(defn tech-init
  []
  (do (tech-clone)
      (tech-copy)))

(comment
  (tech-init))
