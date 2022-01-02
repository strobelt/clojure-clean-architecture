(ns core.use-case.undo-test
  (:require [clojure.test :refer :all]
            [core.entity.todo :as todo]
            [storage.collection :refer [make-storage]]
            [core.use-case.undo :as undo]
            [core.action :as action]))

(deftest undo
  "When updating an existing done to-do"
  (let [title "Some title"
        existing-todo (todo/create title)
        existing-todo (assoc existing-todo :done true)
        id (:id existing-todo)
        input (undo/create-input id)
        storage (make-storage {(keyword id) existing-todo})]
    (undo/execute input (fn [output-action]
                        (let [action-type (::action/type output-action)
                              error? (::action/error? output-action)
                              todo (::action/data output-action)
                              stored-todo (get @(:*coll storage) (keyword id))]
                          (is (= action-type :todo/undo) "Should return 'undo' action type")
                          (is (false? error?) "Should not have errors")
                          (is (= title (:title todo)) "Should not have changed title")
                          (is (false? (:done todo)) "Todo should be not done")
                          (is (= stored-todo todo) "Storage should have the updated todo")
                          (is (not= stored-todo existing-todo) "Storage should not have the todo on the previous state")))
                  storage)))