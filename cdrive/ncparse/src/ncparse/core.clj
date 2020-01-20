;;;; ---------------------------------
;;;;
;;;; Parses NC file
;;;;
;;;;http://www.sec.gov/answers/form13f.htm
;;;;ftp://ftp.sec.gov/edgar/Feed
;;;;http://www.federalreserve.gov/releases/h15/data.htm
;;;; ---------------------------------
(ns ncparse.core
  (:use [clojure.string :as s :exclude [replace reverse join]]
        [clojure.contrib.str-utils]
	[clojure.xml :as xml]
	[ncparse.xml :as ncx]
	[ncparse.ddl :as ncd]))

(import [java.io ByteArrayInputStream])

(def ncdir   "/Documents and Settings/Administrator/My Documents/Downloads/nc/0")
(def ncbad "/tim/clojure/ncparse/db/ncbadwt.xml")
(def ncbig "/tim/clojure/ncparse/db/0000067590-10-000736.nc")

(defn test1
  [ncdir]
  (doseq [file (take 30 (line-seq (clojure.java.io/reader ncdir)))]
    (dprintln (format "\n>>>>>>>>>>>> Parsing %s" file))
    (let [mp (ncx/ncparse file)
	  name (first
		(:content
		 (first
		  (filter #(= :CONFORMED-NAME (:tag %)) (xml-seq mp)))))]
      (println (format "%s - %s" name (count mp))))))

(defn test2
  [ncdir]
  (doseq [file (take 3000 (line-seq (clojure.java.io/reader ncdir)))]
    (let [mp (ncx/ncparse file)
	  ddl (ncd/emit-ddl mp)
	  name (first
		(:content
		 (first
		  (filter #(= :CONFORMED-NAME (:tag %)) (xml-seq mp)))))]
      ddl)))

(defn -main
  [& args]
  (test2 ncdir))


;(load-file "xml.clj")
;(load-file "sqlite.clj")
;(load-file "ddl.clj")

;(load-file "core.clj")
;(in-ns 'ncparse.core)
(-main)
