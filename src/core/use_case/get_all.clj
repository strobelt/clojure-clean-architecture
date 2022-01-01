(ns core.use-case.get-all
  (:require [core.action :as action]
            [core.entity.todo :as todo]))

(defn create-output [todos]
  (action/create :todo/list todos))

(defn execute [output storage]
  (let [todos (todo/get-all storage)]
    (->> todos
         (create-output)
         (output))))