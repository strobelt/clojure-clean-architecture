(ns core.entity.todo_test
  (:require [clojure.test :refer :all]
            [core.entity.todo :as todo]))

(deftest generate-id
  "When generating an id"
  (let [uuid (todo/generate-id)]
    (is (not (empty? (str uuid))) "Should not be empty")))

(deftest create
  "When creating a todo with a title"
  (let [title "Some pretty title"
        new-todo (todo/create title)]
    (is (not (empty? (:id new-todo))) "Should have an id")
    (is (= title (:title new-todo)) "Should have the title sent to the method")
    (is (= false (:done new-todo))) "Should not be done"))
