(defn parting
  "returns a String parting in a given language"
  ([] (parting "World"))
  ([name] (parting name "en"))
  ([name language]
    ; condp is similar to a case statement in other languages.
    ; It is described in more detail later.
    ; It is used here to take different actions based on whether the
    ; parameter "language" is set to "en", "es" or something else.
    (condp = language
      "en" (str "Goodbye, " name)
      "es" (str "Adios, " name)
      (throw (IllegalArgumentException.
        (str "unsupported language " language))))))

(defn parting
  "returns a String parting in a given language"
  ([] (parting "World"))
  ([name] (parting name "en"))
  ([name language]
     (str
      (condp = language
	  "en" "Goodbye"
	  "es" "Adios"
	  (throw (IllegalArgumentException.
		  (str "unsupported language " language))))
      " " name)))

(parting "IBM" "ru")

(import '(javax.swing JFrame JLabel JTextField JButton)
	'(java.awt.event ActionListener)
	'(java.awt GridLayout))

(defn celsius []
  (let [frame (JFrame. "Celsisus Converter")
	 t (JTextField. "0")
	 c (JLabel. "Celsius")
	 b (JButton. "Convert")
	 f (JLabel. "Fahrenheit")]
    (.addActionListener b
      (proxy [ActionListener] []
	(actionPerformed [evt]
	  (.setText f
	    (str (+ 32 (* 1.8 (. Double parseDouble (.getText t))))
	      " Fahrenheit")))))
    (doto frame
      (.setLayout (GridLayout. 2 2 3 3))
      (.add t) (.add c) (.add b) (.add f)
      (.setSize 300 80)
      (.setVisible true))))

(celsius)

(defn f [x]
  (println "calculating f of" x)
  (/ (* x x) 2.0))

; Create an infinite sequence of results from the function f
; for the values 0 through infinity.
; Note that the head of this sequence is being held in the binding "f-seq".
; This will cause the values of all evaluated items to be cached.
(def f-seq (map f (iterate inc 0)))

; Force evaluation of the first item in the infinite sequence, (f 0).
(println "first is" (first f-seq)) ; -> 0.0

; Force evaluation of the first three items in the infinite sequence.
; Since the (f 0) has already been evaluated,
; only (f 1) and (f 2) will be evaluated.
(doall (take 3 f-seq))

(println (nth f-seq 2)) ; uses cached result -> 2.0

(def AND #(and %1 %2))

(def rank (zipmap [- + * / AND =] (iterate inc 1)))

(defn infix* [[a b & [c d e & more]]]
  (println "a=" a "b=" b "c=" c "d=" d "e=" e)
  (cond
   (vector? a) (recur (list* (infix* a) b c d e more))
   (vector? c) (recur (list* a b (infix* c) d e more))
   (ifn? b) (if (and d (< (rank b 0) (rank d 0)))
	      (recur (list a b (infix* (list* c d e more))))
	      (recur (list* (b a c) d e more)))
   :else a))

(defn infix [& args]
  (infix* args))

(infix 21 / [1 + 2 * 3])