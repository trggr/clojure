(ns cljdrv.core
  (:use [clj-webdriver.core] :reload))

;; Start the browser and bind it to `b`
(def b (start :firefox "http://www.odnoklassniki.ru"))

;; Click "Login" link
;;(-> b
;;    (find-it {:text "Login"})
;;    click)

;; Input username/email into the "Login or Email" field
(-> b
    (find-element (by-id "field_email"))
    (input-text "tashepkov@gmail.com"))

;; 
(-> b
    (find-element (by-id "field_password"))
    (input-text "cecegvio"))

;; Click the "Voyti" button
(-> b
    (find-element (by-id "hook_FormButton_button_go"))
    click)

(-> b
    (find-it {:href "/games"})
    click)

(-> b
    (find-it {:href "/game/vday"})
    click)

(-> b
    (find-element (by-id "menu-fight_index"))
    click)

(-> b
    (find-element (by-id "menu-fight_commanders"))
    click)

(-> b
    (find-element (by-id "vday_Fight_BotFight_38"))
    click)
r