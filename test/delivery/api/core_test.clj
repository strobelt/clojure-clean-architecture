(ns delivery.api.core-test
  (:require [clojure.test :refer :all]
            [delivery.api.core :as api]
            [storage.collection :refer [make-storage]]
            [io.pedestal.test :as test]
            [io.pedestal.http :refer [create-servlet]]))

(def test-data {:1 {:id "1" :title "Test" :done false}
                :2 {:id "2" :title "Done Test" :done true}})

(defn populate-storage []
  (reset! (:*coll api/storage) test-data))

(defn clear-storage []
  (reset! (:*coll api/storage) {}))

(defn with-data-in-storage [f]
  (populate-storage)
  (f)
  (clear-storage))

(use-fixtures :each with-data-in-storage)

(deftest todo-get-all
  "When getting all todos"
  (let [fake-server (:io.pedestal.http/service-fn (create-servlet api/service-map))
        response    (test/response-for fake-server :get "/todo")
        status      (:status response)
        body        (:body response)
        body-map    (clojure.edn/read-string body)
        headers     (:headers response)]
    (is (= status 200) "Should return 200 Ok status code")
    (is (not (nil? body)) "Should have a body")
    (is (= body-map (vals test-data)) "The body should contain the test data in storage")
    (is (contains? headers "Content-Type"))) "Should have a content type")

(deftest todo-post
  "When posting a todo"
  (let [fake-server  (:io.pedestal.http/service-fn (create-servlet api/service-map))
        todo-title   "Posted Todo"
        response     (test/response-for fake-server :post (str "/todo?title=" todo-title))
        status       (:status response)
        body         (:body response)
        created-todo (clojure.edn/read-string body)
        created-id   (:id created-todo)
        headers      (:headers response)
        storage-data @(:*coll api/storage)]
    (is (= status 201) "Should return 201 Created status code")
    (is (not (nil? body)) "Should have a body")
    (is (= (:title created-todo) todo-title) "The body returned should have the title sent")
    (is (= ((keyword created-id) storage-data) created-todo) "Should have the created todo in the storage")
    (is (contains? headers "Content-Type") "Should have a content type")))

(deftest todo-do-post
  "When doing a todo"
  (let [fake-server  (:io.pedestal.http/service-fn (create-servlet api/service-map))
        not-done     (first (filter #(false? (:done %)) (vals test-data)))
        not-done-id  (:id not-done)
        response     (test/response-for fake-server :post (str "/todo/do/" not-done-id))
        status       (:status response)
        body         (:body response)
        updated-todo (clojure.edn/read-string body)
        headers      (:headers response)
        storage-data @(:*coll api/storage)
        stored-todo  ((keyword not-done-id) storage-data)
        ]
    (is (= status 200) "Should return 200 Ok status code")
    (is (not (nil? body)) "Should have a body")
    (is (true? (:done updated-todo)) "Should return the done todo")
    (is (true? (:done stored-todo)) "Should store the done todo")
    (is (= stored-todo updated-todo) "Should return the same todo as stored")
    (is (contains? headers "Content-Type") "Should have a content type")))

(deftest todo-undo-post
  "When undoing a todo"
  (let [fake-server  (:io.pedestal.http/service-fn (create-servlet api/service-map))
        done     (first (filter #(true? (:done %)) (vals test-data)))
        done-id  (:id done)
        response     (test/response-for fake-server :post (str "/todo/undo/" done-id))
        status       (:status response)
        body         (:body response)
        updated-todo (clojure.edn/read-string body)
        headers      (:headers response)
        storage-data @(:*coll api/storage)
        stored-todo  ((keyword done-id) storage-data)
        ]
    (is (= status 200) "Should return 200 Ok status code")
    (is (not (nil? body)) "Should have a body")
    (is (false? (:done updated-todo)) "Should return the undone todo")
    (is (false? (:done stored-todo)) "Should store the undone todo")
    (is (= stored-todo updated-todo) "Should return the same todo as stored")
    (is (contains? headers "Content-Type") "Should have a content type")))
