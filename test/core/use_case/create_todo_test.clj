(ns core.use-case.create-todo-test
  (:require [clojure.test :refer :all]
            [core.use-case.create-todo :as create]
            [core.action :as action]
            [storage.collection :refer [make-storage]])
  (:use clojure.pprint))

(deftest create
  (let [title "Some title"
        input (create/create-input title)
        storage (make-storage)]
    (create/execute input (fn [output-action]
                            (let [action-type (::action/type output-action)
                                  error? (::action/error? output-action)
                                  todo (::action/data output-action)]
                              (is (= (action-type :todo/create)))
                              (is (false? error?))
                              (is (= (:title todo) title))
                              (is (= false (:done todo)))
                              (is (contains?  @(:*coll storage) todo))))
                    storage)))