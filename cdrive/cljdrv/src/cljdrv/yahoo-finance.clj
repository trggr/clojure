(ns cljdrv.core
  (:use [clj-webdriver.core] :reload))

;; Start the browser and bind it to `b`
(def b (start :firefox "http://finance.yahoo.com"))

;; Click "Login" link
;;(-> b
;;    (find-it {:text "Login"})
;;    click)

;; Input username/email into the "Login or Email" field
(-> b
    (find-element (by-id "txtQuotes"))
    (input-text "HIG"))

;; Input password into the "Password" field
;;(-> b
;;    (find-it {:xpath "//input[@id='password']"}) ; :xpath and :css options
;;    (input-text "password"))

;; Click the "Get Quotes" button
(-> b
    (find-it :input {:type "submit" :value "Get Quotes"})
    click)
