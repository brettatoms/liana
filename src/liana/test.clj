(ns liana.test
  (:require [liana.system :as system]
            [integrant.core :as ig]))

;; TODO: Create a fixture so we can also halt the system when we're done

(defn system [& {:keys [profile]
                 :or {profile :test}
                 :as opts}]
  (let [cfg (-> (system/config profile opts)
                ;; Don't start the Jetty server for the test client
                (dissoc :liana.core/server))]
    (ig/load-namespaces cfg)
    (-> cfg
        (ig/prep)
        (ig/init))))

(defn client [& {:as opts}]
  (let [sys (system opts)]
    (:liana.core/app sys)))
