;;;;----------------------------------------------------------
;;;; ddl.clj - generates and deploys DDL
;;;;----------------------------------------------------------
(ns ncparse.ddl
  (:use [clojure.string :as s :exclude [replace reverse join]]
        [clojure.contrib.str-utils]
	[clojure.xml :as xml]
	[clojure.set :as set]
	[ncparse.sqlite :as sql]))

;;
;; ID generator
;;
(let [ID (ref 0)
      set-id (fn [x] (dosync (ref-set ID x)))]
  (defn nextval []
    (set-id (inc @ID))
    @ID)
  (defn reset-id []
    (set-id 0)))

(defn seg?
  "Returns true, if e is a segment of a tree"
  [e]
  (when e
    (map? ((:content e) 0))))

(defn csv
  "Separates the items of a sequence by an arbitrary value"
  [coll val]
  (if (empty? coll)
    coll
    (reduce #(conj %1 val %2) [(first coll)] (rest coll))))

(defn sql-name
  "Converts a keyword into a nice table or column name suitable for SQL usage"
  [kw]
  (s/replace (.toLowerCase (name kw)) "-" "_"))

(defn ddl-create-table
  "Retruns SQL create statement"
  [tbl cols]
  (list
   (str "create table " tbl " (\n"
	(apply str "  " (csv (keys cols) " varchar(50),\n  "))
	" varchar(50)\n);")))

(defn ddl-alter-table
  "Returns a list of SQL commands to add missing columns to tbl"
  [tbl cols present]
  (map #(str "alter table " tbl " add " %1 " varchar(50);")
       (set/difference (apply hash-set (keys cols)) present)))

(defn ddl-create-or-alter
  "Creates (or alters) a database table"
  [tbl cols]
  (let [present (sql/tblcols tbl)]
    (if present
      (ddl-alter-table tbl cols present)
      (ddl-create-table tbl cols))))

(defn ddl-insert
  "Retuns SQL insert statement for table tab with columns cols"
  [tab cols]
  (list
   (str "insert into " tab " ("
       (apply str (csv (keys cols) ", "))
       ")\nvalues ('"
       (apply str (csv (vals cols) "', '"))
       "');")))

(defn emit-ddl
  "Returns DDL for a given file"
  ([e parent]
     (let [[fk fkid] parent
	   tab (sql-name (:tag e))
	   nodes (filter (complement seg?) (:content e))
	   pk   (str tab "_id")
	   pkid (nextval)
	   z1 (-> (zipmap (map #(sql-name (:tag %)) nodes)
			  (map #(first (:content %)) nodes))
		  (assoc pk pkid))
	   cols (if fk (assoc z1 fk fkid) z1)
	   dd (ddl-create-or-alter tab cols)]
       (when-not (empty? dd)
	 (apply sql/execute dd))
       (apply sql/execute (ddl-insert tab cols))
       (doseq [s (filter seg? (:content e))]
	 (emit-ddl s (vector pk pkid)))))
  ([e]
     (emit-ddl e nil)))
