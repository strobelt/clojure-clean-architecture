(ns core.use-case.create-test
  (:require [clojure.test :refer :all]
            [core.use-case.create :as create]
            [core.action :as action]
            [storage.collection :refer [make-storage]]))

(deftest create
  "When creating a todo"
  (let [title "Some title"
        input (create/create-input title)
        storage (make-storage)]
    (create/execute input (fn [output-action]
                            (let [action-type (::action/type output-action)
                                  error? (::action/error? output-action)
                                  todo (::action/data output-action)
                                  stored-todo (get @(:*coll storage) (keyword (:id todo))) ]
                              (is (= action-type :todo/create) "Should return 'create' action type")
                              (is (false? error?) "Should not have errors")
                              (is (= (:title todo) title) "Should have the title sent to the method")
                              (is (= false (:done todo)) "Todo should not be created as done")
                              (is (= stored-todo todo)) "Todo stored should be the same as the one returned"))
                    storage)))