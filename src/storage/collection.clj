(ns storage.collection
  (:require [core.entity.todo :as todo]))

(defn- save [*todos todo]
  (swap! *todos assoc (keyword (:id todo)) todo)
  todo)

(defn- fetch [*todos id]
  (get @*todos (keyword id)))

(defn- get-all [*todos]
  (vals @*todos))

(defrecord CollectionStorage [*coll])
(extend-type CollectionStorage
  todo/TodoStorage
  (-save [this todo] (save (:*coll this) todo))
  (-fetch [this todo-id] (fetch (:*coll this) todo-id))
  (-get-all [this] (get-all (:*coll this))))

(defn make-storage
  ([map]
   (->CollectionStorage (atom map)))
  ([]
   (make-storage {})))