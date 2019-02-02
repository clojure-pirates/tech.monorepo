(defproject tech/monorepo "0.1.4"
  :description "libraries for machine learning"
  :url "http://www.github.com/techascent/tech.monorepo"
  :license {:name "MIT License"}
  :source-paths ["src" "dev"]
  :java-source-paths ["java"]
  :profiles {:dev {:plugins [[lein-ancient "0.6.15"]
                             [lein-exec "0.3.7"]
                             [cider/cider-nrepl "0.18.0"]]
                   :dependencies [[hara/code       "3.0.5"]
                                  [hara/deploy    "3.0.5"]
                                  [hara/io.file   "3.0.5"]
                                  [hara/lib.jgit  "3.0.5"]
                                  [hara/module.namespace  "3.0.5"]
                                  [hara/test      "3.0.5"]
                                  [hara/tool      "3.0.5"]]}}
  :injections   [(require 'hara.tool)]
  :dependencies [[org.clojure/clojure "1.9.0"]
  
                 ;; core.config
                 [environ/environ "1.1.0"]
                 [org.clojure/java.classpath "0.3.0"]
                 [org.clojure/spec.alpha "0.1.143"]
                 [org.clojure/core.specs.alpha "0.1.24"]
                 
                 ;; core.jna
                 [net.java.dev.jna/jna "5.0.0"]
                 
                 ;; core.parallel
                 [org.clojure/core.async "0.4.490"]
                 
                 ;; datatype
                 [net.mikera/core.matrix "0.62.0"]
                 [net.mikera/vectorz-clj "0.48.0"]
                 
                 ;; io
                 [clj-commons/fs "1.5.0"]
                 [com.taoensso/nippy  "2.15.0-alpha8"]
                 [com.taoensso/timbre "4.10.0"]
                 
                 ;; ml
                 [camel-snake-kebab "0.4.0"]
                 [org.apache.commons/commons-math3 "3.6.1"]
                 
                 ;;lib.fastutil
                 [it.unimi.dsi/fastutil "8.1.0"]
                 
                 ;; lib.javacpp
                 [org.bytedeco/javacpp "1.4.4"]
                 
                 ;; lib.mxnet
                 [org.apache.mxnet.contrib.clojure/clojure-mxnet-linux-cpu "1.3.0"]
                 
                 ;; lib.opencv
                 [org.bytedeco.javacpp-presets/opencv-platform "4.0.1-1.4.4"]
                 
                 ;; lib.smile
                 [com.github.haifengl/smile-core "1.5.2"]
                 
                 ;; lib.tablesaw
                 [tech.tablesaw/tablesaw-core "0.30.2"]
                 
                 ;; lib.xgboost
                 [me.lyh/xgboost4j "0.72-20180627-1214081f"]])