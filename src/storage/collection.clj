(ns storage.collection
  (:require [core.entity.todo :as todo]))

(defn- save [*todos todo]
  (swap! *todos conj todo)
  todo)

(defrecord CollectionStorage [*coll]
  todo/TodoStorage
  (-save [_ todo] (save *coll todo)))

(defn make-storage
  ([coll]
   (->CollectionStorage (atom (set coll))))
  ([]
   (make-storage #{})))