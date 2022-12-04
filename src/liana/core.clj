(ns liana.core
  (:require [liana.middleware :as middleware]
            [liana.system :as system]
            [integrant.core :as ig]
            [reitit.ring :as r.ring]
            [ring.adapter.jetty9 :as jetty]
            [ring.middleware.defaults :as defaults]
            [ring.middleware.session.cookie :refer [cookie-store]]))

(def ^:dynamic *request* nil)

(defn bind-request-middleware
  "Middleware that binds the current request to *request*"
  [handler]
  (fn [request]
    (binding [*request* request]
      (handler request))))

(defmethod ig/init-key ::router [_ {:keys [defaults request-context routes cookie-secret]}]
  (let [cookie-store-options {:key (.getBytes cookie-secret)}
        defaults (-> {:site defaults/site-defaults
                      :secure-site defaults/secure-site-defaults
                      :api defaults/api-defaults
                      :secure-api defaults/secure-api-defaults}
                     (get defaults)
                     (assoc-in [:session :store]
                               (cookie-store cookie-store-options)))
        router-options {:data {:middleware [;; [middleware/exception-middleware]
                                            [defaults/wrap-defaults defaults]
                                            [middleware/wrap-request-context request-context]
                                            [middleware/wrap-coerce-response]
                                            [bind-request-middleware]]}
                        ;; :conflicts (constantly nil) ;; allow /* routes
                        }]
    (r.ring/router routes router-options)))

(defmethod ig/init-key ::app [_ {:keys [router]}]
  (r.ring/ring-handler router (r.ring/routes ;(r.ring/create-resource-handler {:path "/"})
                               (r.ring/create-default-handler)
                               (r.ring/redirect-trailing-slash-handler {:method :strip}))))

(defmethod ig/init-key ::server [_ {:keys [app port]}]
  ;; TODO: Allow passing any jetty options from, see
  ;; https://github.com/sunng87/ring-jetty9-adapter/blob/e606b98264ee309cd25553df16423cfd8988a24e/src/ring/adapter/jetty9.clj#L262
  (jetty/run-jetty app
                   {:port port
                    :join? false}))

(defmethod ig/halt-key! ::server [_ server]
  (jetty/stop-server server))

(def start #'system/start)
(def stop #'system/stop)
