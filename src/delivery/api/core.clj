(ns delivery.api.core
  (:use clojure.pprint)
  (:require [clojure.data.json :as json]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [core.use-case.get-all :as get-all]
            [core.action :as action]
            [storage.collection :refer [make-storage]]
            [delivery.api.coerce-body-interceptor :refer [coerce-body]]
            ))

(defn response [status body & {:as headers}]
  {:status status :body body :headers headers})

(def ok (partial response 200))
(def created (partial response 201))
(def accepted (partial response 202))
(def not-found (partial response 400))

(def echo
  {:name  ::echo
   :enter (fn [context]
            (let [request  (:request context)
                  response (ok request)]
              (assoc context :response response)))})

(def todo-get-all
  {:name  ::todo-get-all
   :enter (fn [context]
            (let [storage (make-storage {:1 {:id 1 :title "Test"}})
                  output  (fn [action]
                            (println "\n\nCONTEXT INSIDE ACTION")
                            (pprint (get-in context [:request :headers "accept"]))
                            (pprint (assoc context :response (ok action)))
                            (assoc context :response (ok action)))]
              (println "\n\nCONTEXT")
              (pprint (get-in context [:request :headers "accept"]))
              (println "\n\n")
              (get-all/execute output storage)))})

(def routes
  (route/expand-routes
    #{["/todo" :get [coerce-body todo-get-all]]
      ["/todo" :post echo :route-name :todo-create]
      ["/todo/do/:todo-id" :post echo :route-name :todo-do]
      ["/todo/undo/:todo-id" :post echo :route-name :todo-undo]}))

(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8080})

(defn start []
  (http/start (http/create-server service-map)))

(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (http/start (http/create-server
                        (assoc service-map
                          ::http/join? false)))))

(defn stop-dev []
  (http/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))

(restart)

;(defn -main []
;  (start))

;(route/try-routing-for routes :prefix-tree "/todo" :get)
