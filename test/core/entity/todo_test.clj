(ns core.entity.todo_test
  (:require [clojure.test :refer :all]
            [core.entity.todo :as todo]))

(deftest create
  (let [title "Some pretty title"
        new-todo (todo/create title)]
    (is (= title (:title new-todo)))
    (is (= false (:done new-todo)))))
