(ns liana.template-test
  (:require [clojure.test :refer [deftest is]]
            [liana.template :as template]
            [liana.core :as core]))

(deftest render-string-test
  (is (= (template/render-string "hi") "hi"))
  (is (= (template/render-string "{{ x }}" {:x "hi"})  "hi"))
  (is (= (template/render-string "{{ x }}" {"x" "hi"})  "hi")))

(deftest current-request-var-test
  (binding [core/*request* {:request-method "get"}]
    (is (= (template/render-string "{{ request.request-method }}") "get"))))
