;
;; Solutions to 4clojure.com riddles
;;
(fn [coll n]
  "Find nth element of a sequence"
  (loop [i 0 c coll]
    (if (= i n)
      (first c)
      (recur (inc i) (rest c)))))



(fn [coll]
   "Returns the total number of elements in a sequence."
   (loop [n 0 c coll]
     (if-not c
       n
       (recur (inc n) (next c)))))


(fn [coll]
  "Reverses a sequence"
  (loop [acc '() c coll]
    (if-not c
      acc
      (recur (conj acc (first c))
	     (next c)))))

(fn [coll]
  "Returns the sum of a sequence of numbers"
  (reduce + coll))


(fn [coll]
  "Returns only the odd numbers from a sequence"
  (filter odd? coll))

(fn [n]
  "Returns the first Fibonacci numbers"
  (loop [i 0 fib []]
    (if (= i n)
      fib
      (recur (inc i)
	     (cond (= i 0) (conj fib 1)
		   (= i 1) (conj fib 1)
		   :else (conj fib (+ (fib (- i 1))
				      (fib (- i 2)))))))))

(fn [coll]
  "Returns true if coll is palindrome"
  (let [s (seq coll)]
    (= (reverse s) s)))


(fn [coll]
  "Flattens a sequence"
  (letfn [(f [acc coll]
	     (println (format "acc=%s, coll=%s" acc coll))
	     (let [e (first coll)]
	       (cond (nil? e)  acc
		     (coll? e) (f (f acc e) (rest coll))
		     :else (f (conj acc e) (rest coll)))))]
    (reverse (f '() coll))))


(fn [x]
  "Takes a string and returns a new string containing only the capital letters"
  (apply str (filter #(Character/isUpperCase %) (seq x))))


(fn [coll]
  "Removes consecutive duplicates from a sequence"
  (loop [prev nil s coll acc []]
    (let [cur (first s)]
      (cond (nil? cur) acc
        (= cur prev) (recur prev (rest s) acc)
	:else (recur cur (rest s) (conj acc cur))))))

(fn [coll]
  "Packs consecutive duplicates into sub-lists"
  (partition-by identity coll))

(fn [coll]
  "Duplicates each element of a sequence"
  (loop [acc [] c coll]
    (let [p (first c)]
      (if (nil? p)
	acc
	(recur (conj acc p p) (rest c))))))

(defn dup-n [coll n]
  "Duplicates each element of a sequence n times"
  (loop [acc [] c coll]
    (let [p (first c)]
      (if (nil? p)
	acc
	(recur (apply conj acc (for [i (range 0 n)] p)) (rest c))))))

(fn [coll n]
  "Duplicates each element of a sequence n times"
  (loop [acc [] c coll]
    (let [p (first c)]
      (if (nil? p)
	acc
	(recur (apply conj acc (for [i (range 0 n)] p)) (rest c))))))

(fn [n m]
  "Creates a list of all integers in a given range"
  (loop [i n acc []]
    (if (= i m)
      acc
      (recur (inc i) (conj acc i)))))

;; "Let in Be" solution to bind x y z
[z 1 x 7 y 3]

;; Regular expressions
"ABC"

(fn [& args]
  "Returns a maximum among its arguments"
  (reduce #(if (> %1 %2) %1 %2) (first args) args))

(fn [a b]
  "Interleaves two sequences"
  (flatten (map #(list %1 %2) a b)))

;; Not a solution, but useful function
(defn map2
  "Takes fn and sequence. And calls fn upon pair elements of a sequence;
   fn should accept two arguments"
  [f coll]
  (loop [acc []
	 s coll]
    (let [e (first s) g (second s)]
      (cond (nil? e) acc
	    (nil? g) (recur (conj acc e) (rest s))
	    :else (recur (conj acc (f e g))
			 (rest s))))))
	
(fn [val coll]
  "Separates the items of a sequence by an arbitrary value"
  (if (empty? coll)
    coll
    (reduce #(conj %1 val %2) [(first coll)] (rest coll))))

(fn [coll n]
  "drops every n-th item from a sequence"
  (loop [acc [] s coll i 1]
    (let [e (first s)]
      (if-not e
	acc
	(recur (if (= 0 (mod i n)) acc (conj acc e))
	       (rest s)
	       (inc i))))))

(defn tmp [n]
  "Calculates factorial"
  (loop [acc 1 i 1]
    (if (> i n)
      acc
      (recur (* acc i) (inc i)))))

(fn [n]
  "Calculates factorial"
  (loop [acc 1 i 1]
    (if (> i n)
      acc
      (recur (* acc i) (inc i)))))

(defn tmp [coll n]
  "Reverses the interleave process into n number of subsequences"
  (loop [acc [] s coll cur 0]
    (let [e (first s)]
      (if-not e
	acc
	(recur (assoc acc cur
		      (conj (if (> cur (dec (count acc))) [] (acc cur))
		       e))
	       (rest s)
	       (mod (inc cur) n))))))

(fn [coll n]
  "Reverses the interleave process into n number of subsequences"
  (loop [acc [] s coll cur 0]
    (let [e (first s)]
      (if-not e
	acc
	(recur (assoc acc cur
		      (conj (if (> cur (dec (count acc))) [] (acc cur))
		       e))
	       (rest s)
	       (mod (inc cur) n))))))

(fn [n v]
  "Rotates sequence n times. If n < 0 rotates right, if n > 0 rotates left"
  (let [m (dec (count v))
	step (cond (zero? n) 0
		   (< n 0) 1
		   :else -1)]
    (loop [i n acc (if (vector? v) v (vec v))]
      (if (zero? i)
	acc
	(recur (+ i step)
	       (if (< n 0)
		 (vec (list* (last acc) (subvec acc 0 m)))
		 (conj (subvec acc 1) (first acc))))))))

;; Solution to "Intro to iterate"
'(1 4 7 10 13)


;; Solution to "Contain yourself"
4

;; Solution to "Intro to some"
6

(fn [f]
  "Flips the order of arguments of f"
  (fn [x y]
    (f y x)))

(fn [n a]
  "Split a sequence into two parts"
  (loop [acc [] s a i n]
    (let [e (first s)]
      (if (zero? i)
	(vector acc (vec s))
	(recur (conj acc e)
	       (rest s)
	       (dec i))))))

;; Solution to "Split by Type"
(fn [a]
  "Split a sequence by type"
  (vals (group-by type a)))

;; Solution to "Intro to Destructuring"
[c e]

;; Solution to "Advanced Destructuring"
'(1 2 3 4 5)

(fn [x]
  "Find a longest increasing sub-sequence"
  (letfn [(compete
	   [wannabe established]
	   (if (> (count wannabe) (count established))
	     wannabe established))]
    (loop [candidate [] champ [] a x]
      (let [e (first a)]
	(if-not e
	  (let [rc (compete candidate champ)] (if (> (count rc) 1) rc []))
	  (if (or (empty? candidate) (<= e (peek candidate)))
	    (recur [e]  (compete candidate champ)  (rest a))
	    (recur (conj candidate e)  champ  (rest a))))))))

(fn [n x]
  "Returns a sequence of lists of n items each.
   Lists of less than x items not returned"
  (loop [acc [] a x i 0 part []]
    (let [e (first a)]
      (if-not e
	(if (= n (count part))
	  (conj acc part)
	  acc)
	(if (= i n)
	  (recur (conj acc part)
		 (rest a)
		 1
		 [e])
	  (recur acc
		 (rest a)
		 (inc i)
		 (conj part e)))))))

(defn tmp [x]
  "Returns a map containing the number of occurences of each distinct item in a sequence"
  (loop [acc {} a (group-by identity x)]
    (let [e (first a)]
      (if-not e
	acc
	(recur (assoc acc (key e) (count (val e)))
	       (rest a))))))

(fn [x]
  "Returns a map containing the number of occurences of each distinct item in a sequence"
  (loop [acc {} a (group-by identity x)]
    (let [e (first a)]
      (if-not e
	acc
	(recur (assoc acc (key e) (count (val e)))
	       (rest a))))))

;; Solution to "Simple Recursion"
'(5 4 3 2 1)

(fn [x]
  "Returns distinct values in the sequence. Maintains order"
  (loop [acc [] a x]
    (let [e (first a)]
      (if-not e
	acc
	(recur (if (some #(= e %) acc) acc (conj acc e))
	       (rest a))))))

(fn [& funcs]
  "Creates function composition. Takes any number of functions
   and applies them to an argument from right to left"
  (fn [& args]
    (let [fs (reverse funcs)]
      (loop [acc (apply (first fs) args) fs (rest fs)]
	(let [f (first fs)]
	  (if f
	    (recur (f acc) (rest fs))
	    acc))))))

(fn [& funcs]
  "Takes a set of functions and returns a new function that takes a variable
   number of arguments and returns a sequence containing the result of
   applying each function left-to-right to the argument list"
  (fn [& args]
    (map #(apply %1 args) funcs)))


;; Solution to "Intro to Reduce"
+

(defn tmp [f s]
  "Behaves like reduce, but returns each intermediate value of the reduction"
  (loop [acc [] rc (first s) a s]
    (let [e (first a)]
      (if e
	(let [x (f rc e)]
	  (recur (conj acc x)
		 x
		 (next a)))
	acc))))

(defn tmp
  "Behaves like reduce, but returns each intermediate value of the reduction"
  ([f init sq]
     (loop [acc [init] rc init a sq]
       (let [e (first a)]
	 (if e
	   (let [x (f rc e)]
	     (recur (conj acc x)
		    x
		    (next a)))
	   acc))))
  ([f sq]
     (tmp f (first sq) (rest sq))))

;;  "Behaves like reduce, but returns each intermediate value of the reduction"
(fn tmp
  ([f coll]
     (lazy-seq
      (if-let [s (seq coll)]
        (tmp f (first s) (rest s))
        (list (f)))))
  ([f init coll]
     (cons init
           (lazy-seq
            (when-let [s (seq coll)]
              (tmp f (f init (first s)) (rest s)))))))


(fn [keys vals]
  "Takes a vector of keys and a vector of values and constructs a map from them"
  (loop [acc {} ks keys vs vals]
    (let [k (first ks) v (first vs)]
      (if-not (and k v)
	acc
	(recur (assoc acc k v) (rest ks) (rest vs))))))


(fn tmp [f x]
  "Takes f and initial x, and returns lazy seq of x, (f x), (f (f x)), etc"
  (cons x
	(lazy-seq (tmp f (f x)))))

;; Solution to "Rearranging Code: ->"
last


;; Solution to "Rearranging Code: ->>"
reduce +

(fn [x y]
  "Returns the greatest common divisor"
  (let [stop (min x y)]
    (loop [rc 1 i 2]
      (if (> i stop)
	rc
	(recur (if (and (zero? (rem x i)) (zero? (rem y i))) i rc)
	       (inc i))))))

;; "Recurring Theme"
[7 6 5 4 3]

(fn tmp [f s]
  "Given a function f and a sequence s, write a function which returns a map.
  The keys should be the values of f applied to each item in s. The value at
  each key should be a vector of corresponding items in the order they
  appear in s"
  (loop [acc {} a s]
    (let [e (first a)]
      (if-not e
	acc
	(let [k (f e)]
	  (recur (assoc acc k
			(conj
			 (if (empty? (acc k)) [] (acc k)) e))
		 (rest a)))))))


(fn [s]
  "Write a function which splits a sentence up into a sorted
  list of words. Capitalization should not affect sort order
  and punctuation should be ignored"
  (sort #(compare (.toUpperCase %1) (.toUpperCase %2))
	(re-seq #"\w+" s)))

(fn [s]
  "Given a string of comma separated integers,
   write a function which returns a new comma
   separated string that only contains the
   numbers which are perfect squares"
  (let [a (filter #(let [x (Math/sqrt (. Integer parseInt %))]
		     (= x (int x)))
		  (re-seq #"\d+" s))]
    (reduce #(str %1 "," %2) (first a) (rest a))))

(fn [n]
  "Returns the first n prime numbers"
  (loop [acc [] i 2]
    (if (= (count acc) n)
      acc
      (recur (if (some #(= 0 (rem i %)) (range 2 i))
	       acc
	       (conj acc i))
	     (inc i)))))


;; Solution to "Intro to Trampoline"
[1 3 5 7 9 11]

(fn [n]
  "A number is perfect if the sum of its divisors equal the number itself.
   6 is a perfect number because 1+2+3=6. Write a function which returns
   true for perfect numbers and false otherwise"
  (= n 
     (reduce + (filter #(= 0 (rem n %)) (range 1 (dec n))))))

(fn [s t]
  "Returns common set of items, which both s and t have"
  (set (filter #(t %) s)))

(fn [& args]
  "Takes a variable number of booleans. Returns true if some
   of the parameters are true, but not all of the parameters are true.
   Otherwise, returns false"
  (if (and (not (every? identity args))
	   (some identity args))
    true false))

(fn [s]
  "Write a function which takes a collection and returns one of
  :map, :set, :list, or :vector - describing the type of collection
  it was given. You won't be allowed to inspect their class or use
  the built-in predicates like list? - the point is to poke at them
  and understand their behavior."
  (cond (not (ifn? s)) :list
	(= (first (conj s {:z 1})) [:z 1]) :map
	(associative? s) :vector
	:else :set))

(defn tmp [s t]
  "Returns common set of items, which are in one set but not in both"
  (let [a (set (filter #(not (t %)) s))
	b (set (filter #(not (s %)) t))]
    (loop [acc a x b]
      (let [e (first x)]
	(if-not e
	  acc
	  (recur (conj acc e) (rest x)))))))

(defn tmp [s t]
  "Returns common set of items, which are in one set but not in both"
  (loop [acc (set (filter #(not (t %)) s))
	 x (set (filter #(not (s %)) t))]
    (let [e (first x)]
      (if-not e
	acc
	(recur (conj acc e) (rest x))))))

(fn tmp [s t]
  "Returns common set of items, which are in one set but not in both"
  (loop [acc (set (filter #(not (t %)) s))
	 x (set (filter #(not (s %)) t))]
    (let [e (first x)]
      (if-not e
	acc
	(recur (conj acc e) (rest x))))))

(fn tmp [s t]
  "Returns Cartesion product"
  (set (for [i s j t] [i j])))

    
  ;; (loop [acc (set (filter #(not (t %)) s))
  ;; 	 x (set (filter #(not (s %)) t))]
  ;;   (let [e (first x)]
  ;;     (if-not e
  ;; 	acc
  ;; 	(recur (conj acc e) (rest x))))))

(fn tmp [s]
  "Anagram finder"
  (set
   (map set
	(vals
	 (filter #(> (count (val %)) 1)
		 (group-by #(sort (seq %)) s))))))

(fn tmp [f & maps]
  "Takes a function f and a variable number of maps.
   Returns a map that consists of the rest of the maps conj-ed
   onto the first. If a key occurs in more than one map, the mapping(s)
   from the latter (left-to-right) should be combined with the mapping
   in the result by calling (f val-in-result val-in-latter)"
  (let [ks (set (flatten (map keys maps)))
	vs (map (fn [x] (apply f (filter identity x)))
		(map (fn [k] (map (fn [a] (a k)) maps))
		     ks))]
    (zipmap ks vs)))

(fn tmp [f & maps]
  "Takes a function f and a variable number of maps.
   Returns a map that consists of the rest of the maps conj-ed
   onto the first. If a key occurs in more than one map, the mapping(s)
   from the latter (left-to-right) should be combined with the mapping
   in the result by calling (f val-in-result val-in-latter)"
  (letfn [(fmerge [f mp1 mp2]
		  (loop [a mp1 b mp2]
		    (let [e (first b)]
		      (if-not e
			a
			(let [k (key e) v (val e)]
			  (recur (assoc a k (if (a k) (f (a k) v) v))
				 (rest b)))))))]
    (loop [acc {} mp maps]
      (let [e (first mp)]
	(if-not e
	  acc
	  (recur (fmerge f acc e)
		 (rest mp)))))))

(fn tmp [triangle]
  "Calculates a minimal path from the top of
   triangle to the bottom:
             [[1]
             [2 4]
            [5 1 4]
           [2 3 4 5]]
   Path is 1 + 2 + 1 + 2 = 6"
  (letfn [(min-index [v]
	   "Returns index on a minimal element of v"
	   (loop [mink 0 minv (v 0) i 1]
	     (if (>= i (count v))
	       mink
	       (let [e (v i)
		     rotate (< e minv)]
		 (recur (if rotate i mink)
			(if rotate e minv)
			(inc i))))))]
    (loop [path (ffirst triangle) index 0 level (subvec triangle 1)]
      (let [e (first level)]
	(if-not e
	  path
	  (let [from (max 0 index)
		choice (subvec e from (min (+ 2 from) (count e)))
		mini (+ index (min-index choice))]
	    (println (format "path = %s, index = %s, level = %s, e = %s, choice = %s" path index level e choice))
	    (recur (+ path (e mini)) mini (subvec level 1))))))))

    
(fn tmp [n]
  "Verifies if a number is happy"
  (letfn [(int-digits [m]
		    (map #(- (int %) 48) (seq (str m))))]
    (loop [rc n hops 0]
      (cond (= rc 1) true
	    (> hops 100) false
	    :else (recur (reduce + (map #(* % %) (int-digits rc)))
			 (inc hops))))))

(defn tmp [board]
  "Analyzes Tic-Tac-Toe board"
  (letfn [(all3 [v brd]
		some identity (map #(= v %) brd))]
    (let [a (apply conj board
		   (for [f [first second last]] (map f board)))
	  b (conj a (for [i [0 1 2]] ((board i) i)))
	  c (conj b (for [i [0 1 2]] ((board (- 2 i)) i)))]
      (cond (all3 [:x :x :x] c) :x
	    (all3 [:o :o :o] c) :o
	    :else nil))))

(fn tmp [board]
  "Analyzes Tic-Tac-Toe board"
  (letfn [(all3 [v brd]
		(some #(= true %) (map #(= [v v v] %) brd)))]
    (let [a (apply conj board
		   (for [f [first second last]] (map f board)))
	  b (conj a (for [i [0 1 2]] ((board i) i)))
	  c (conj b (for [i [0 1 2]] ((board (- 2 i)) i)))]
      (cond (all3 :x c) :x
	    (all3 :o c) :o
	    :else nil))))

 123456
1x 
2 x
3  x
4   x
5    x
6     x

1 -> 2 -> 3 -> 1
4 -> 5 -> 6 -> 4
