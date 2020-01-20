;;;;
;;;; Displays, side-by-side, the days of a data loads in two environments
;;;;

(ns probe.core
  (:use clojure.set)
  (:use clojure.contrib.sql))

(def devdb
  {:classname "com.ibm.db2.jcc.DB2Driver"
    :subprotocol "db2"
    :subname "//btscqm02.boulder.ibm.com:50000/cqm_dw:retrieveMessagesFromServerOnGetMessage=true;"
    :user "cqmetl"
    :password "fun2work"})

(def prodb
  {:classname "com.ibm.db2.jcc.DB2Driver"
    :subprotocol "db2"
    :subname "//b03cxnp01103.gho.boulder.ibm.com:60001/cqm_prod"
    :user "cqmetl"
    :password "moh00ple"})

(defn run-query [q]
  (def res (with-connection prodb
	     (with-query-results res [q] (into [] res))))
  res)

(defn run-query1 [q]
  (with-connection prodb
    (with-query-results res [q] (into [] res)))
  res)

(defn run-query2 [q]
  (with-connection prodb
    (with-query-results rs [q]
      (doseq [row rs]
	(println rs)))))

(defn run-query3 [q]
  (with-connection prodb
    (with-query-results rs [q]
      (into [] rs))))

(defn main []
  (println 
    (run-query3 (slurp "/tim/clojure/probe/src/probe/sql/etl-batch.sql"))))

(defn main-2 []
  (run-query3 (slurp "/tim/clojure/probe/src/probe/sql/etl-batch.sql")))

(def x (main-2))
  
;(doseq [r x]
;  (doseq [[key val] r]
;    (print val))
;  (println))
;
; probably a simpler, more efficient approach
;(defn save-db [db filename] (spit filename (with-out-str (print db))))
;(defn load-db [filename] (with-in-str (slurp filename) (read)))
