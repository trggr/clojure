(use 'clojure.test)

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


(deftest pat-match
  (is (= (pat-match '(i need a :x) '(i need a vacation))
 	 {:x 'vacation, :true true})))

   
  (let [a (struct animal 10 20 30 0 '(1 2 3 4 5 6 7 8))
	b (struct animal 20 30 30 0 '(1 2 3 4 5 6 7 8))
	anim (list a b)
	plants '#{(10 20) (20 30)}]
    (is (= (first (meal anim plants))
	   (list (struct animal 20 30 110 0 '(1 2 3 4 5 6 7 8))
		 (struct animal 10 20 110 0 '(1 2 3 4 5 6 7 8)))))
    (is (= (second (meal anim plants)) #{}))))

(pat-match '(i need a :x) '(i need a vacation))
(pat-match '(i need a :x) '(i really need a vacation))
(pat-match '(i need a :x and :y) '(i need a vacation and rest))