(ns tech.monorepo
  (:require [hara.lib.jgit :refer [git] :as git]
            [hara.io.file :as fs]))

(def +url+ "https://github.com/techascent/%s.git")

(def +libraries+
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

(def +dirs+ ["src" "java" "test"])

(defn tech-url
  [id]
  (format +url+ id))

(defn tech-repo
  [id & more]
  (apply str "original/" id more))

(defn tech-clone
  ([]
   (mapv tech-clone +libraries+))
  ([id]
   (if-not (fs/exists? (tech-repo id))
     (git :clone :uri (tech-url id) :directory (tech-repo id)))))

(defn tech-copy
  ([]
   (doall (for [id  +libraries+
                dir +dirs+]
            (tech-copy id dir))))
  ([id dir]
   (let [source-root  (fs/path (tech-repo id) "/" dir)
         files (fs/select source-root
                          {:include [fs/file?]})]
     (mapv (fn [source]
             (let [path    (fs/relativize source-root source)
                   target  (fs/path dir path)]
               (fs/copy-single  source target
                                {:options #{:replace-existing}})))
           files))))

(defn tech-init
  []
  (do (tech-clone)
      (tech-copy)))

(comment
  
  (tech-init)
  
    
  )
