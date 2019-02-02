(ns tech.monorepo
  (:require [hara.lib.jgit :refer [git] :as git]
            [hara.io.file :as fs]
            [hara.io.project :as project]
            [hara.code :as code]))

(def +library-url+ "https://github.com/techascent/%s.git")

(def +library-path+ "original")

(def +library+
  ["tech.datatype"
   "tech.config"
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

(def +rename+
  '[[tech.io.s3             tech.aws.s3]
    [tech.queue.sqs         tech.aws.sqs]
    [tech.datatype.javacpp  tech.lib.javacpp]
    [tech.datatype.fastutil tech.lib.fastutil.datatype]
    [tech.compute.fastutil  tech.lib.fastutil.compute]
    [tech.datatype.tablesaw tech.lib.tablesaw.datatype]
    [tech.compute.tablesaw  tech.lib.tablesaw.compute]
    [tech.xgboost           tech.lib.xgboost]
    [tech.svm               tech.lib.svm]
    [tech.mxnet             tech.lib.mxnet]
    [tech.opencv            tech.lib.opencv]
    [tech.smile             tech.lib.smile]
    [tech.jna               tech.core.jna]
    [tech.resource          tech.core.resource]
    [tech.config.core       tech.core.config]
    [tech.parallel          tech.core.parallel]
    [tech.compute.tvm       tech.lib.tvm]
    [tvm-clj                tech.lib.tvm.interop]
    [tech.ml-base           tech.ml]
    [tech.verify.ml         tech.ml.verify]])

(defn tech-rename
  ([]
   (doall (for [pair  +rename+]
            (tech-rename pair))))
  ([[old new]]
   (project/in-context (code/ns-rename [old new] {:print {:function true}
                                                  :write true}))))

(defn tech-init
  []
  (do (tech-clone)
      (tech-copy)
      (tech-rename)))


(comment

  ;; Clone, Copy and Rename Clojure files:
  (tech-init)
  
  
  ;; Manually Change Java Files:
  ;;  tech.svm -> tech.lib.svm
  ;;  tech.resource -> tech.core.resource

  ;; Change config/deploy.edn, uncomment to use example keys and repositories instead of secured files
  ;;
  ;; In order to secure the files, an additional key needs to be installed. I'll go through
  ;; how to do that.

  ;; See what files are package
  (hara.deploy/linkage '[tech] {:tag :dev})

  ;; Package libs, output to target/interim
  (hara.deploy/package '[tech] {:tag :dev})

  ;; Installs libs to local directory
  (hara.deploy/install '[tech] {:tag :public})

  ;; Deploys libs to clojars/other repository
  (hara.deploy/deploy  '[tech] {:tag :public}))
