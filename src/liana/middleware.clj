(ns liana.middleware)

;; TODO: Setup ring.middleware.http-response/wrap-http-response

;; TODO: If a string is returned assume its html

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

              :else
              response))]
    (fn [request]
      (let [response (handler request)]
        (coerce response)))))
