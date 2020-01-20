;;;;----------------------------------------------------------
;;;; sqlite.clj - sqlite related functions
;;;;----------------------------------------------------------
(ns ncparse.sqlite
  (:use [clojure.string :as s :exclude [replace reverse join]]
	[clojure.contrib.sql :as sql]
	[clojure.contrib.str-utils]))

(def DEBUG true)

(def DB {:classname "org.sqlite.JDBC"
	 :subprotocol "sqlite"
	 :subname "C:/tim/clojure/ncparse/db/nc.sqlite3"})

(defn query
  [q]
  "Queries the database. Uses DB to connect"
  (when DEBUG
    (println "Running: " q))
  (with-connection DB
    (with-query-results res [q]
      (into [] res))))

(defn tblcols
  "Returns list of columns for a given table"
  [tbl]
  (let [q (try
	    (query (str "pragma table_info(" tbl ")"))
	    (catch java.sql.SQLException e nil))]
    (when-not (empty? q)
      (apply hash-set (map :name q)))))

(defn execute
  "Execute SQL command in the database"
  [& cmds]
  (when cmds
    (when DEBUG (println "Running: " cmds))
    (with-connection DB
      (apply sql/do-commands cmds))))
