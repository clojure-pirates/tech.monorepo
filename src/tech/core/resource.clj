(ns tech.core.resource
  "'off-heap' resource management.  Default management is stack based management but gc
  or a combination of the two is possible.  To declare a stack region use
  stack-resource-context.  No matter how the context unwinds, stack resources declared
  within will be released.

  More options for stack based resources available at tech.core.resource.stack.

  For GC resources, users must not reference the tracked object in the dispose function
  else the circular dependency will keep the object in the gc's live set"
  (:require [tech.core.resource.stack :as stack]
            [tech.core.resource.gc :as gc]))


(defn- normalize-track-type
  [track-type]
  (let [track-type (or track-type :stack)]
    (if (keyword? track-type)
      #{track-type}
      (set track-type))))


(defmulti track-impl
  "Internal implementation to differentiate between different forms of tracking
  resources."
  (fn [item dispose-fn track-type]
    track-type))


(defmethod track-impl #{:stack}
  [item dispose-fn _]
  (stack/track item dispose-fn))


(defmethod track-impl #{:gc}
  [item dispose-fn _]
  (gc/track-gc-only item dispose-fn))


(defmethod track-impl #{:gc :stack}
  [item dispose-fn _]
  (gc/track item dispose-fn))


(defn track
  "Track a resource.  If the item inherents from PResource or is a clojure fn, or a
  Runnable object then it can be cleaned up by the stack system with no further dispose
  function.  Objects tracked by the gc need to have a dispose fn that does *not*
  reference the tracked object.
  Track types can be :gc, :stack, or [:gc :stack]."
  [item & [dispose-fn track-type]]
  (let [track-type (normalize-track-type track-type)]
    (when (and (contains? track-type :gc)
               (not dispose-fn))
      (throw (ex-info "gc track types must have a dispose function that does *not*
reference item."
                      {:item item
                       :track-type track-type})))
    (let [dispose-fn (or dispose-fn item)]
      (when-not (or (satisfies? stack/PResource dispose-fn)
                    (fn? dispose-fn)
                    (instance? Runnable dispose-fn))
        (throw (ex-info "The dispose method must implement PResource, be Runnable, or a
clojure function."
                        {:dispose-fn dispose-fn})))
      (track-impl item dispose-fn track-type))))


(defmacro stack-resource-context
  "Stack resource context.  When this context unwinds, stack resource declared within
  will be released."
  [& body]
  `(stack/with-resource-context ~@body))
