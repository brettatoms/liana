(ns liana.system
  (:require [integrant.core :as ig]))

(def profile-defaults
  {:router/defaults {:default :secure-site
                     :test :site
                     :local :site}
   :router/cookie-secret {:default "1234567890123456"}
   :server/port {:default 3000}})

(defn config [& {:keys [profile]
                 :or {profile :default}
                 :as opts}]
  (let [profile-opts (reduce-kv #(assoc %1 %2 (get %3 profile (get %3 :default)))
                                {} profile-defaults)
        opts (merge profile-opts opts)]
    {:liana.core/router {:cookie-secret (:router/cookie-secret opts)
                         :request-context (merge {:db (ig/ref :liana.db/pool)}
                                                 (:router/request-context opts))
                         :routes (:router/routes opts)
                         :defaults (:router/defaults opts)}
     :liana.core/app {:router (ig/ref :liana.core/router)}
     :liana.db/pool {:db-spec (:db/spec opts)
                     :jdbc-options (:db/options opts)}
     :liana.core/server {:port (:server/port opts)
                         :app (ig/ref :liana.core/app)}}))

(defn start [& {:as opts}]
  (let [cfg (config opts)]
    (ig/load-namespaces cfg)
    (-> cfg
        (ig/prep)
        (ig/init))))

(defn stop [system]
  (ig/halt! system))
