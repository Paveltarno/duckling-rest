(ns duckling-rest.handler
  (:gen-class)
  (:use compojure.core ring.middleware.json)
  (:require [compojure.handler :as handler])
  (:require [duckling.core :as p])
  (:require
  	[compojure.route :as route]
    [ring.adapter.jetty :refer :all]
  )
)

(defn default-lang
  []
  (or (System/getenv "DEFAULT_LANG") "en")
)

(defn port
  []
  (Integer/parseInt
    (or ( System/getenv "PORT") 
        "9000")
  )
)

(defn host
  []
  (or (System/getenv "IP") 
      "0.0.0.0")
)

(defn parse [text-to-parse & [language dim]]
  (p/parse 
    (str (or language (default-lang)) "$core") text-to-parse dim
  )
)

(defn parse-time [text-to-parse lang]
  (parse text-to-parse lang [:time])
)

(defn parse-number [text-to-parse lang]
  (parse text-to-parse lang [:number])
)

(defn parse-ordinal [text-to-parse lang]
  (parse text-to-parse lang [:ordinal])
)

(defn parse-duration [text-to-parse lang]
  (parse text-to-parse lang [:duration])
)

(defroutes app-routes
  (GET "/parse/all/:text" [text lang] (parse text lang))
  (GET "/parse/time/:text" [text lang] (parse-time text lang))
  (GET "/parse/number/:text" [text lang] (parse-number text lang))
  (GET "/parse/ordinal/:text" [text lang] (parse-ordinal text lang))
  (GET "/parse/duration/:text" [text lang] (parse-duration text lang))

  (POST "/parse/all" {:keys [params]} (let [{:keys [text lang]} params] (parse text lang)))
  (POST "/parse/time" {:keys [params]} (let [{:keys [text lang]} params] (parse-time text lang)))
  (POST "/parse/number" {:keys [params]} (let [{:keys [text lang]} params] (parse-number text lang)))
  (POST "/parse/ordinal" {:keys [params]} (let [{:keys [text lang]} params] (parse-ordinal text lang)))
  (POST "/parse/duration" {:keys [params]} (let [{:keys [text lang]} params] (parse-duration text lang)))
  (route/not-found "Not Found")
)

(def app
      (-> (handler/api app-routes)
        (wrap-json-body)
        (wrap-json-params)
        (wrap-json-response))
)

(defn -main [& args]
  (println "Loading Duckling server 🦆 🦆 🦆")
  (p/load!)
  (run-jetty app {
    :host (host)
    :port (port)
  }))


; :dim  examples
; temperature   “70°F”
; “72° Fahrenheit”
; “thirty two celsius”
; “65 degrees”
; distance  “8miles”
; “3 feet”
; “2 inches”
; “3’’“
; “4km”
; “12cm”
; volume  “250ml”
; “2liters”
; “1 gallon”
; amount-of-money   “ten dollars”
; “4 bucks”
; “$20”
; email   “help@wit.ai”
; url   “http://wit.ai”
; “www.foo.com:8080/path”
; “https://myserver?foo=bar”
; “cnn.com/info”
; “foo.com/path/path?ext=%23&foo=bla”
; “localhost”
; phone-number  “415-123-3444”
; “+33 4 76095663”
; “(650)-283-4757 ext 897”