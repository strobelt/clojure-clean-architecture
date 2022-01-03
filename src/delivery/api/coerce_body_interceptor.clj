(ns delivery.api.coerce-body-interceptor
  (:require [clojure.data.json :as json]))

(defn accepted-type
  [context]
  (println "\n\nCABECAS" (get-in context [:request :headers]))
  (get-in context [:request :headers "accept"] "text/plain"))

(defn transform-content [body content-type]
  (case content-type
    "*/*" body
    "text/html" body
    "text/plain" body
    "application/edn" (pr-str body)
    "application/json" (json/write-str body)))

(defn coerce-to
  [response content-type]
  (-> response
      (update :body transform-content content-type)
      (assoc-in [:headers "Content-Type"] content-type)))

(defn no-content-type? [context]
  (let [existing-content-type (get-in context [:response :headers "Content-Type"])]
    (nil? existing-content-type)))

(defn add-content-type [context]
  (update-in context [:response] coerce-to (accepted-type context)))

(def coerce-body
  {:name ::coerce-body
   :leave
   (fn [context]
     (cond-> context
             (no-content-type? context) add-content-type))})
