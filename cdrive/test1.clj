(defn f1 [x]
  (map (fn [g] (g x)) [inc
		       dec
		       even?
		       (partial + 10)
		       #(Math/abs %)]))

(+ 10 20)

(def tweet "This is an example tweet talking about clojure and emacs.")
(def regex (re-pattern
	    (apply str (interpose "|"
				  ["clojure" "logic" "compojure" "emacs" "macros"]))))

(re-seq regex tweet)


(use 'clojure.contrib.profile)

(time
 (profile
  (reduce (fn [a x]
	    (if (even? x)
	      (prof :add (+ a x))
	      a))
	  (range 1 100))))


(defn and*
  "Returns true, if x and y are true"
  ([x y]
     (if x
       (if y y false)
       false))
  ([x y & more]
     (loop [acc (and* x y) args more]
       (cond (empty? args) acc
	     (not (first args)) false
	     :else (recur (and* acc (first args))
			  (rest args))))))

(defn or*
  "Returns true, if x or y are true"
  ([x y]
     (if x
       x
       (if y true false)))
  ([x y & more]
     (loop [acc (or* x y) args more]
       (cond (empty? args) acc
	     (first args) true
	     :else (recur (or* acc (first args))
			  (rest args))))))


;; map returns 9100
;; pmap returns 3125
(time
 (doall
  (pmap #(count (slurp %))
	["http://gdrd80.watson.ibm.com/cqm/ume-report/ume-report-AG.log"
	 "http://gdrd80.watson.ibm.com/cqm/ume-report/ume-report-EMEA.log"
	 "http://gdrd80.watson.ibm.com/cqm/ume-report/ume-report-AP.log"
	 "http://gdrd80.watson.ibm.com/cqm/prism-report/prism-report-AG.log"
	 "http://gdrd80.watson.ibm.com/cqm/prism-report/prism-report-EMEA.log"
	 "http://gdrd80.watson.ibm.com/cqm/prism-report/prism-report-AP.log"])))

(def counter (agent 0))
(send counter inc)
@counter

(def aca-sev1 (agent []))
(send aca-sev1 (fn [] (ca-sev1 "2011-03-01")))
