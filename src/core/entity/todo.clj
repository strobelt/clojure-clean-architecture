(ns core.entity.todo
  (:import (java.util UUID)))

(defn generate-id []
  (str (UUID/randomUUID)))

(defn create [title]
  {:id (generate-id)
   :title title
   :done false})

(defprotocol TodoStorage
  (-save [storage todo] "Save a todo")
  (-fetch [storage todo-id] "Gets a todo by id")
  (-get-all [storage] "Gets all todos"))

(defn save [storage todo]
  (-save storage todo))

(defn fetch [storage id]
  (-fetch storage id))

(defn get-all [storage]
  (-get-all storage))
