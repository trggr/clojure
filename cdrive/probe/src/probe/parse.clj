;;;;
;;;; Parses SQL table syntax
;;;;

(ns probe.parse
  (:use clojure.set))

;(def *grammar* 
;  '( (tab     $name)
;     (col     $name (coltype)(colname type mandatory))
;     (colname  %)
;     (type      (integer varchar timestamp))
;     (mandatory (null not-null))
;     (PK         (colname))
;     (index*     (() (index)))
;     (index      col*)))

(def *grammar*
  '( (rm     $m    (=> r $m))      ; register -> memory
     (mr     $m    (=> $m r))      ; memory -> register
     (load   const (=> const r))   ; constant -> register
     (add    $m    (=> r (+ $m r))) ; memory + register -> register
     (inc          (=> r (+ 1 r)))     ; increament register
     (dec          (=> r (- 1 r)))     ; decrement register
     ))

(defn mappend [fn coll]
  "Apply fn to each element of coll and append the results"
  (reduce concat (map fn coll)))

(defn cl-assoc [key coll]
  (if (= 0 (count coll))
    nil
    (let [elt (first coll)]
      (if (= key (first elt))
	(rest elt)
	(cl-assoc key (rest coll))))))
      
(defn rule-lhs [rule] (first rule))
(defn rule-rhs [rule] (rest (rest rule)))
;; Returns a list of the possible rewrites for this category
(defn rewrites [category]
  (rule-rhs (cl-assoc category *grammar*)))

(defn parse [phrase]
  (println ">>>>" phrase)
  (cond
    (list? phrase)
    (do
      (parse (first phrase))
      (parse (rest phrase)))
    
    (= 'rm phrase) (println "RM found!")
    (= 'mr phrase) (println "MR found!")
    (let [choises (rewrites phrase)]
      (if choises
	(do
	  (println phrase "-> " choises)
	  choises)
	(list phrase)))
    (:else phrase)))
