(ns core.action)

(defn create
  "Create a new successful action"
  ([type data]
   {::type type
    ::error? false
    ::data data})
  ([type]
   {::type type
    ::error? false}))

(defn create-error
  "Create a new error action"
  ([type data]
   {::type type
    ::error? true
    ::data data})
  ([type]
   {::type type
    ::error? true}))
