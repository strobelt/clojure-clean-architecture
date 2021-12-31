(ns core.entity.todo)

(defn create [title]
  {:title title
   :done false})

(defprotocol TodoStorage
  (-save [this todo] "Save a todo"))

(defn save [storage todo]
  (-save storage todo))
