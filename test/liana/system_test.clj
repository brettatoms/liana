(ns liana.system-test
  (:require [liana.system :as system]
            [clojure.test :refer [deftest is]]))

(deftest config-test
  ;; selmer cache is enabled by default
  (is (true? (-> (system/config)
                 :liana.template/selmer
                 :enable-cache)))
  ;; selmer cache is disable for the local profile
  (is (false? (-> (system/config :profile :local)
                  :liana.template/selmer
                  :enable-cache)))
  ;; uses the default port
  (is (= 3000
         (-> (system/config)
             :liana.core/server
             :port)))
  ;; allows overriding the port
  (is (= 9999
         (-> (system/config :profile :local :server/port 9999)
             :liana.core/server
             :port))))
