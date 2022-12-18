(ns liana.db
  (:require  [integrant.core :as ig]
             [next.jdbc :as jdbc]
             [next.jdbc.connection :as jdbc.connection]))

(defmethod ig/init-key ::pool [_ {:keys [db-spec jdbc-options]}]
  (when db-spec
    ;; HikariCP expects :username
    (let [db-spec (cond-> db-spec
                    (not (:username db-spec))
                    (assoc :username (:user db-spec))

                    ;; When using postgresql use :connectionInitSql "COMMIT;" setting is required in case
                    ;; a default :schema is provided, see https://github.com/brettwooldridge/HikariCP/issues/1369
                    (= (:db-type db-spec) "postgresql")
                    (assoc :connectionInitSql "COMMIT;"))
          pool (jdbc.connection/->pool
                com.zaxxer.hikari.HikariDataSource
                db-spec)]
      (jdbc/with-options pool jdbc-options))))
