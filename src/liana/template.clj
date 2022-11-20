(ns liana.template
  (:require
   [integrant.core :as ig]
   [liana.core :as core]
   [selmer.parser :as selmer]))

(defn make-context [ctx]
  (merge {:request core/*request*}
         ctx))

(defn render-template
  ([template]
   (render-template template {}))
  ([template context]
   (selmer/render-file template (make-context context))))

(defn render-string
  ([template]
   (render-string template {}))
  ([template context]
   (selmer/render template (make-context context))))

(defmethod ig/init-key ::selmer [_ {:keys [enable-cache template-path]
                                    :or {enable-cache true}}]
  (if enable-cache
    (selmer.parser/cache-on!)
    (selmer.parser/cache-off!))

  (selmer.parser/set-resource-path! template-path)
  nil)
