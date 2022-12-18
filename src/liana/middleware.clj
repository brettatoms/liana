(ns liana.middleware
  (:require [clojure.pprint :as pp]
            [rum.core :as rum]
            [reitit.ring.middleware.exception :as exception]))

;; TODO: Setup ring.middleware.http-response/wrap-http-response

;; TODO: Pretty page when template render fails

(defn wrap-request-context [handler global-context]
  (fn [request]
    (-> request
        (assoc :context global-context)
        handler)))

(defn wrap-coerce-response [handler]
  (letfn [(coerce [response]
            (cond
              (string? response)
              {:status 200
               :headers {"content-type" "text/html"}
               :body (str response)}

              ;; Response expected to be [status body]
              (and (sequential? response)
                   (= 2 (count response)))
              {:status (first response)
               :body (second response)}

              ;; Response expected to be [status body headers]
              (and (sequential? response)
                   (= 3 (count response)))
              {:status (first response)
               :body (second response)
               :headers (nth response 2)}

              (map? response)
              (merge {:status 200
                      :body ""
                      :headers {"content-type" "text/html"}}
                     response)

              :else
              response))]
    (fn [request]
      (let [response (handler request)]
        (coerce response)))))

(defn exception-handler [message exception _request & rest]
  (let [markup (rum/render-static-markup
                [:div
                 [:h1 {:class "text-xl"} message]
                 [:pre
                  (with-out-str
                    (pp/pprint exception))]])]
    {:status 500
     :headers {"content-type" "text/html"}
     :body markup}))

(def exception-middleware
  (exception/create-exception-middleware
   (merge
    exception/default-handlers
    {;; ex-data with :type ::error
     ::error (partial exception-handler "error")

     ;; ex-data with ::exception or ::failure
     ::exception (partial exception-handler "exception")

     ;; SQLException and all it's child classes
     java.sql.SQLException (partial exception-handler "sql-exception")

     ;; override the default handler
     ::exception/default (partial exception-handler "default")

     ;; print stack-traces for all exceptions
     ::exception/wrap (fn [_handler e request]
                        (exception-handler (or (ex-message e) "Unknown error") e request)
                        ;; (exception-handler "wrap" e request)
                        ;; (println "ERROR" (pr-str (:uri request)))
                        ;; (tap> e)
                        ;; (tap> (ex-message e))
                        ;; (tap> (ex-data e))
                        ;; (tap> (ex-cause e))
                        ;; (handler e request)
                        )
     ;; (partial exception-handler "wrap")
     })))
