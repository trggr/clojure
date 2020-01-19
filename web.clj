(use 'clojure.contrib.str-utils)
(use 'clojure.string)

(def x (slurp "http://news.ycombinator.com"))

;; (def words (re-split #"\s+" x))
;; (def x1 (subs x 0 2000))

(def x2 (->> x
             (re-gsub #"<script>(.|\n)*?</script>" "")
             (re-gsub #"<(.|\n)*?>" "")))

;; (def x2 (re-gsub #"<(.|\n)*?>" "" x))

x2

;;
;;(re-split #"<.+>" x1)
;;(def words (re-split #"\s+" (re-gsub #"<.+>" "" x)))
;;(spit "/tim/xx" (seq words))
