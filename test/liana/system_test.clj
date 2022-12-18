(ns liana.system-test
  (:require [liana.system :as system]
            [clojure.test :refer [deftest is]]))

(deftest config-test
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
