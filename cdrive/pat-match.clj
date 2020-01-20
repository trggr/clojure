(ns pat-match
  (:use clojure.test))

;; Indicates pat-match failure
(def fail nil)

;; Indicates pat-match success, with no variables
(def no-bindings {:true true})

(defn get-binding
  [v bindings]
  "Find a (variable value) pair in a binding list"
  (let [x (v bindings)]
    (when x [v x])))

(defn binding-val
  [binding]
  "Get the value part of a single binding"
  (second binding))

(defn lookup
  [v bindings]
  "Get the value part (for var) from a binding list"
  (binding-val (get-binding v bindings)))

(defn extend-bindings
  [v val bindings]
  "Add a var val pair to the binding list"
  (assoc bindings v val))

(defn match-variable
  [v input bindings]
  "Does var match input? Uses (or updates) and returns bindings"
  (let [binding (get-binding v bindings)]
    (cond (nil? binding) (extend-bindings v input bindings)
	  (= input (binding-val binding)) bindings
	  :else fail)))

(defn variable?
  [x]
  "Is x a variable (a symbol beginning with '?')?"
  (keyword? x))

(defn pat-match
  ([pattern input]
     "Match pattern agains input in the context of bindings"
     (pat-match pattern input no-bindings))
  ([pattern input bindings]
     (cond (= bindings fail) fail
	   (variable? pattern) (match-variable pattern input bindings)
	   (= pattern input) bindings
	   (and (seq? pattern) (seq? input))
	   (pat-match (rest pattern) (rest input)
		      (pat-match (first pattern) (first input)
				 bindings))
	   :else fail)))

(deftest test-pat-match
  (is (= (pat-match '(i need a :x) '(i need a vacation))
 	 {:x 'vacation, :true true}))
  (is (= (pat-match '(i need a :x) '(i really need a vacation))
	 nil))
  (is (= (pat-match '(i need a :x and :y) '(i need a vacation and rest))
	 {:x 'vacation, :y 'rest, :true true}))
  (is (= (pat-match '(this is easy) '(this is easy))
	 {:true true}))
  (is (= (pat-match '(:x is :x) '((2 + 2) is 4))
	 nil))
  )

(run-tests 'pat-match)
