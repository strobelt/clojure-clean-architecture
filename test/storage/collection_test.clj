(ns storage.collection-test
  (:require [clojure.test :refer :all]
            [storage.collection :refer :all]
            [core.entity.todo :refer :all]))

(deftest make-storage-with-empty-coll
  "When a storage with an empty collection"
  (let [storage (make-storage)
        stored-items @(:*coll storage)]
    (is (empty? stored-items) "Should have no items")))

(deftest make-storage-with-existing-coll
  "When creating a storage with an existing collection"
  (let [existing-collection {:a 1 :b 2}
        storage (make-storage existing-collection)
        stored-items @(:*coll storage)]
    (is (= existing-collection stored-items) "Should have the existing items")))

(deftest save-new-item
  "When saving an item that does not exist in the collection"
  (let [item {:id "1" :name "Test"}
        storage (make-storage)
        saved-item (-save storage item)
        stored-items @(:*coll storage)
        existing-item (first (vals stored-items))]
    (is (= (count stored-items) 1) "The storage should have a single item stored")
    (is (= item existing-item) "The item sent should be the one stored")
    (is (= item saved-item) "The item saved should be returned")))

(deftest save-existing-item
  "When saving an item that already exists in the collection"
  (let [item {:id "1" :name "Test"}
        storage (make-storage {:1 item})
        changed-item {:id "1" :name "Changed" :new "prop"}
        saved-item (-save storage changed-item)
        stored-items @(:*coll storage)
        existing-item (first (vals stored-items))]
    (is (= (count stored-items) 1) "The storage should still have a single item stored")
    (is (= changed-item existing-item) "The changed item sent should be the one stored")
    (is (= changed-item saved-item) "The changed item saved should be returned")))

(deftest fetch-existing-item
  "When fetching an existing item"
  (let [id "1"
        item {:id id :title "Test"}
        storage (make-storage {(keyword id) item})
        item-fetched (-fetch storage id)]
    (is (= item item-fetched) "Should return the item with the same id on the storage")))

(deftest fetch-non-existing-item
  "When fetching a non-existing item"
  (let [id "1"
        storage (make-storage)
        item-fetched (-fetch storage id)]
    (is (nil? item-fetched) "Should return nil")))

(defn create-items [title]
  [(keyword title) {:id title :title title}])

(deftest get-all-with-no-items
  "When getting all items on a collection with no items"
  (let [storage (make-storage)
        all-items (-get-all storage)]
    (is (empty? all-items) "Should return an empty collection")))

(deftest get-all-with-existing-items
  "When getting all items on a collection with items"
  (let [titles (map str (range 100))
        items (apply hash-map (apply concat (mapv create-items titles)))
        storage (make-storage items)
        all-items (-get-all storage)]
    (is (= (vals items) all-items) "Should return all items")))
