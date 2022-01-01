(ns core.use-case.undo
  (:require [core.entity.todo :as todo]
            [core.action :as action]))

(defn create-input [todo-id]
  {:id todo-id})

(defn create-output [todo]
  (action/create :todo/undo todo))

(defn execute [input output storage]
  (let [id (:id input)
        existing (todo/fetch storage id)
        updated (update existing :done (constantly false))]
    (->> updated
         (todo/save storage)
         (create-output)
         (output))))
