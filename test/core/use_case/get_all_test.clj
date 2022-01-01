(ns core.use-case.get-all-test
  (:use clojure.pprint)
  (:require [clojure.test :refer :all]
            [core.entity.todo :as todo]
            [storage.collection :refer [make-storage]]
            [core.use-case.get-all :as get-all]
            [core.action :as action]))

(defn todo->keyedtodo [todo]
  [(keyword (:id todo)) todo])

(deftest get-all-existing
  "When listing all todos in a storage"
  (let [titles (map str (range 100))
        todos (mapv todo/create titles)
        keyed-todos (apply concat (map todo->keyedtodo todos))
        storage (make-storage (apply hash-map keyed-todos))]
    (get-all/execute (fn [output-action]
                       (let [action-type (::action/type output-action)
                             error? (::action/error? output-action)
                             returned-todos (::action/data output-action)
                             stored-todos (vals @(:*coll storage))]
                         (is (= action-type :todo/list) "Should return 'list' action type")
                         (is (false? error?) "Should not have errors")
                         (is (= (frequencies returned-todos) (frequencies stored-todos)) "Should return all stored todos")
                         (is (= (frequencies todos) (frequencies returned-todos)) "Should return all todos")))
                     storage)))
