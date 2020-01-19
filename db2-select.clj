(use ['clojure.contrib.sql :as 'sql])

(def db {:classname "com.ibm.db2.jcc.DB2Driver"
	 :subprotocol "db2"
	 :subname "//b03cxnp01103.gho.boulder.ibm.com:60001/cqm_prod:retrieveMessagesFromServerOnGetMessage=true;"
	 :user "cqmetl"
	 :password "moh00ple"})

(def res (with-connection db
	   (with-query-results res
	     ["select e.*
               from busintel.etl_batch e
               where e.process_name = 'IPC-INCIDENT-EMEA'
               order by e.etl_batch_id desc
               fetch first 5 rows only"]
	     (into [] res))))

(type res)
;; -> clojure.lang.PersistentVector

(map :etl_batch_id res)
;; -> (4014 3944 3848 3690 3601)

(reduce + (map :etl_batch_id res))
;; -> 19097

(reduce #(+ (:etl_batch_id %2) %1) 0 res)
;; -> 19097

(count res)
;; -> 5

(count (subvec res 2 5))
;; -> 3 

;(with-connection db 
;   (with-query-results rs ["select * from busintel.d_metric"] 
;     ; rs will be a sequence of maps, 
;     ; one for each record in the result set. 
;     (dorun (map #(println (:title %)) rs))))
(with-connection db
  (with-query-results res ["select * from busintel.d_metric"]
     (doseq [rec res]
        (println rec))))

(defn hash-filter [pred coll]
  (reduce (fn [m x]
	    (assoc m (pred x)
		   (cons x (get m (pred x) '()))))
	  {} coll))
