# tech.monorepo

A monorepo implementation for https://github.com/techascent/ directories

## Usage

After starting `lein`

```
(require '[tech.monorepo :as mono])

;; will pull down the repos and copy `src`, `test` and `java` directories
(mono/tech-init) 
```

## License

Copyright © 2019