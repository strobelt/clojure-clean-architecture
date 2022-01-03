(ns delivery.api.core
  (:require [clojure.data.json :as json]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [core.use-case.get-all :as get-all-todos]
            [core.use-case.create :as create-todo]
            [core.use-case.do :as do-todo]
            [core.use-case.undo :as undo-todo]
            [storage.collection :refer [make-storage]]
            [delivery.api.coerce-body-interceptor :refer [coerce-body]]
            ))

(defn response [status body & {:as headers}]
  {:status status :body body :headers headers})

(def ok (partial response 200))
(def created (partial response 201))

(defonce storage (make-storage))

(def echo
  {:name  ::echo
   :enter (fn [context]
            (let [request  (:request context)
                  response (ok request)]
              (assoc context :response response)))})

(def todo-get-all
  {:name  ::todo-get-all
   :enter (fn [context]
            (let [output (fn [action] (assoc context :response (ok action)))]
              (get-all-todos/execute output storage)))})

(def todo-create
  {:name  ::todo-create
   :enter (fn [context]
            (let [title  (get-in context [:request :params :title])
                  input  (create-todo/create-input title)
                  output (fn [action] (assoc context :response (created action)))]
              (create-todo/execute input output storage)))})

(def todo-do
  {:name  ::todo-do
   :enter (fn [context]
            (let [id    (get-in context [:request :path-params :todo-id])
                  input (do-todo/create-input id)
                  output (fn [action] (assoc context :response (ok action)))]
              (do-todo/execute input output storage)))})

(def todo-undo
  {:name  ::todo-undo
   :enter (fn [context]
            (let [id    (get-in context [:request :path-params :todo-id])
                  input (do-todo/create-input id)
                  output (fn [action] (assoc context :response (ok action)))]
              (undo-todo/execute input output storage)))})

(def routes
  (route/expand-routes
    #{["/todo" :get [coerce-body todo-get-all]]
      ["/todo" :post [coerce-body todo-create]]
      ["/todo/do/:todo-id" :post todo-do]
      ["/todo/undo/:todo-id" :post todo-undo]}))

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

(defn -main []
  (start))
