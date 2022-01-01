(ns core.use-case.do-test
  (:use clojure.pprint)
  (:require [clojure.test :refer :all]
            [core.entity.todo :as todo]
            [storage.collection :refer [make-storage]]
            [core.use-case.do :as do]
            [core.action :as action]))

(deftest do
  "When updating an existing not done to-do"
  (let [title "Some title"
        existing-todo (todo/create title)
        id (:id existing-todo)
        input (do/create-input id)
        storage (make-storage {(keyword id) existing-todo})]
    (do/execute input (fn [output-action]
                        (let [action-type (::action/type output-action)
                              error? (::action/error? output-action)
                              todo (::action/data output-action)
                              stored-todo (get @(:*coll storage) (keyword id))]
                          (is (= action-type :todo/do) "Should return 'do' action type")
                          (is (false? error?) "Should not have errors")
                          (is (= title (:title todo)) "Should not have changed title")
                          (is (true? (:done todo)) "Todo should be done")
                          (is (= stored-todo todo) "Storage should have the updated todo")
                          (is (not= stored-todo existing-todo) "Storage should not have the todo on the previous state")))
                storage)))