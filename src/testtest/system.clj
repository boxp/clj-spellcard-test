(ns testtest.system
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [testtest.infra.datasource.example :refer [example-datasource-component]]
            [testtest.infra.repository.example :refer [example-repository-component]]
            [testtest.domain.usecase.example :refer [example-usecase-component]]
            [testtest.app.my-webapp.handler :refer [my-webapp-handler-component]]
            [testtest.app.my-webapp.endpoint :refer [my-webapp-endpoint-component]])
  (:gen-class))

(defn testtest-system
  [{:keys [testtest-example-port
           testtest-my-webapp-port] :as conf}]
  (component/system-map
    :example-datasource (example-datasource-component testtest-example-port)
    :example-repository (component/using
                          (example-repository-component)
                          [:example-datasource])
    :example-usecase (component/using
                       (example-usecase-component)
                       [:example-repository])
    :my-webapp-handler (component/using
                         (my-webapp-handler-component)
                         [:example-usecase])
    :my-webapp-endpoint (component/using
                          (my-webapp-endpoint-component testtest-my-webapp-port)
                          [:my-webapp-handler])))

(defn load-config []
  {:testtest-example-port (-> (or (env :testtest-example-port) "8000") Integer/parseInt)
   :testtest-my-webapp-port (-> (or (env :testtest-my-webapp-port) "8080") Integer/parseInt)})

(defn -main []
  (component/start
    (testtest-system (load-config))))
