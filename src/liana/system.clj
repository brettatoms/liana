(ns liana.system
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]))

(def profile-defaults
  {:router/defaults {:default :secure-site
                     :test :site
                     :local :site}
   :router/cookie-secret {:default "1234567890123456"}
   :selmer/template-path {:default (io/resource "templates")}
   :selmer/enable-cache {:default true
                         :local false
                         :test false}
   :server/port {:default 3000}})

(defn config [profile & {:keys [] :as opts}]
  ;; TODO: add a spec here for opts
  (let [profile-opts (reduce-kv #(assoc %1 %2 (get %3 profile (get %3 :default)))
                                {} profile-defaults)
        opts (merge profile-opts opts)]
    {:liana.core/router
     {:cookie-secret (:router/cookie-secret opts)
      :request-context {}
      :routes (:router/routes opts)
      :defaults (:router/router opts)}

     :liana.template/selmer
     {:enable-cache (:selmer/enable-cache opts)
      :template-path (:selmer/template-path opts)}

     :liana.core/app
     {:router (ig/ref :liana.core/router)}

     :liana.core/server
     {:port (:server/port opts)
      :router (ig/ref :liana.core/router)}}))

(defn start [& {:as opts}]
  (let [cfg (config opts)]
    (ig/load-namespaces cfg)
    (-> cfg
        (ig/prep)
        (ig/init))))

(defn stop [system]
  (ig/halt! system))
