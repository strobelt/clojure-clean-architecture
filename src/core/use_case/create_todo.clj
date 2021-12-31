(ns core.use-case.create-todo
  (:require [core.entity.todo :as todo]
            [core.action :as action]))

(defn create-input [todo-title]
  {:title todo-title})

(defn create-output [todo]
  (action/create :todo/create todo))

(defn execute [input output storage]
  (let [title (:title input)
        new-todo (todo/create title)]
    (->> new-todo
         (todo/save storage)
         (create-output)
         (output))))
