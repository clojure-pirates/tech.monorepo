(defproject tech/monorepo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["dev" "src"]
  :profiles {:dev {:plugins [[lein-ancient "0.6.15"]
                             [lein-exec "0.3.7"]
                             [cider/cider-nrepl "0.18.0"]]
                   :dependencies [[hara/lib.jgit "3.0.1"]
                                  [hara/deploy "3.0.1"]]}}
  :dependencies [[org.clojure/clojure "1.9.0"]])