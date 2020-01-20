;;;; ---------------------------------
;;;;
;;;; Zipper around parses NC file
;;;;
;;;; http://www.sec.gov/answers/form13f.htm
;;;; ftp://ftp.sec.gov/edgar/Feed
;;;; http://www.federalreserve.gov/releases/h15/data.htm
;;;; ---------------------------------
(ns ncparse.zip
  (:use [clojure.string :as s :exclude [replace reverse join]]
        [clojure.contrib.str-utils]
	[clojure.xml :as xml]
	[clojure.zip :as zip :exclude [next replace remove]]))


;;;------------------------------
(map (juxt :tag :content) (->> ncbad ncparse zip/xml-zip zip/children))
(def x1
     (str
      "<a>\n"
      "   <b>1</b>\n"
      "   <c>\n"
      "      <d>2</d>\n"
      "      <e>3</e>\n"
      "   </c>\n"
      "</a>"))

(def a (zip/xml-zip (xml/parse (byte-stream x1))))

(defn navigate
  [z]
  (cond (branch? z) zip/down
	:else       (juxt left right)))

(-> a down right down right)

(defn item [x] (first x))
(defn section [x] (second x))

;; What's your name
(defn item-name [x] (:tag (item x)))
(defn item-content [x] (first (:content (item x))))

;; To help visualize XML, transpose it!
;; <a>
;;    <b>1</b>
;;    <c>
;;       <d>2</d>
;;       <e>3</e>
;;    </c>
;; </a>
;;
;;  like this:
;;
;;  a
;;  |
;;  b  --  c 
;;  |      |
;;  1      d -- e
;;         |    |
;;         2    3
;;
;;
;;  or like this:
;;
;; <a>
;;  |
;;  |
;; down
;;  |
;;  V
;; <b>  ---right--->   <c>
;;  |                   |
;;  |                   |
;; down                down
;;  |                   |
;;  V                   V
;;  1                  <d>   -- right --->  (e)
;;                      |                    |
;;                     down                 down
;;                      |                    |
;;                      V                    V
;;                      2                    3

(loop [loc a]
  (if (zip/end? loc)
    (root loc)
    (recur (next loc))))

;; from http://www.exampler.com/blog/2010/09/01/editing-trees-in-clojure-with-clojurezip/
(defn print-tree1 [original]
  (loop [loc (zip/seq-zip (seq original))]
    (if (zip/end? loc)
      (zip/root loc)
      (recur (zip/next
                (do (println (zip/node loc))
                    loc))))))

(require '[clojure.zip :as zip]
	 '[clojure.contrib.lazy-xml :as lxml]
	 '[clojure.contrib.zip-filter.xml :as zf])
