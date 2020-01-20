(use ['clojure.contrib.sql :as 'sql])

(def db {:classname "com.ibm.db2.jcc.DB2Driver"
	 :subprotocol "db2"
	 :subname "//b03cxnp01103.gho.boulder.ibm.com:60001/cqm_prod:retrieveMessagesFromServerOnGetMessage=true;"
	 :user "cqmetl"
	 :password "moh00ple"})

(with-connection db
  (with-query-results res ["select * from busintel.d_metric"]
     (doseq [rec res]
        (println rec))))
