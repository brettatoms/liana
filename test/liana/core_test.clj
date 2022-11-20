(ns liana.core-test
  (:require [liana.test :as test]
            [liana.core :refer [*request*]]
            [clojure.string :as s]
            [clojure.test :refer [deftest is]]
            [spy.assert :as spy.assert]
            [spy.core :as spy]))

(deftest test-request-response
  (let [request-handler (fn [_] "THE RESPONSE")
        path "/"
        routes [[path {:name :root
                       :handler request-handler}]]
        app (test/client :router/routes routes)
        resp (app {:request-method :get :uri path})]
    (is (= 200 (:status resp)))
    (is (s/starts-with? (get-in resp [:headers "content-type"]) "text/html"))
    (is (= "THE RESPONSE" (:body resp)))))

(deftest test-request-binding
  (let [request-handler (spy/spy (fn [request]
                                   (is (= request *request*))
                                   ""))
        path "/"
        routes [[path {:name :root
                       :handler request-handler}]]
        system (test/system :router/routes routes)
        app (:liana.core/app system)
        resp (app {:request-method :get :uri path})]
    (spy.assert/called? request-handler)
    (is (s/starts-with? (get-in resp [:headers "content-type"]) "text/html"))))
