(use 'clojure.contrib.sql)

(def db {:classname "com.ibm.db2.jcc.DB2Driver"
	 :subprotocol "db2"
	 :subname "//btscqm02.boulder.ibm.com:50000/cqm_dw:retrieveMessagesFromServerOnGetMessage=true;"
	 :user "cqmetl"
	 :password "fun2work"})

;;(with-connection db 
;;   (with-query-results rs ["select * from busintel.d_metric"] 
;;     ; rs will be a sequence of maps, 
;;     ; one for each record in the result set. 
;;     (dorun (map #(println (:title %)) rs))))
 

(with-connection db
  (with-query-results res ["select * from busintel.d_metric"]
     (doseq [rec res]
        (println rec))))

