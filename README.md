# tech.monorepo

A monorepo implementation for https://github.com/techascent/ directories

## Usage

Delete `src`, `test`, and `java` folders

```
(require '[tech.monorepo :as mono])

;; Will pull down the repos and copy `src`, `test` and `java` directories

;; Clone, Copy and Rename Clojure files:
(mono/tech-init)

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
(hara.deploy/deploy  '[tech] {:tag :public})
```

## License

Copyright © 2019