(defn memoize [f]
  (let [mem (atom {})]
    (fn [& args]
      (if-let [e (find @mem args)]
        (val e)
        (let [ret (apply f args)]
          (swap! mem assoc args ret)
          ret)))))

(defn dbl [x]
  (print (format "x=%s\n" x))
  (+ x x))

(def memoized-dbl (memoize dbl))

(defn memoize [f]
  (let [mem (atom {})]
    (fn [& args]
      (print (format "mem=%s\n" @mem))
      (if-let [e (find @mem args)]
        (val e)
        (let [ret (apply f args)]
          (swap! mem assoc args ret)
          ret)))))

(defn dbl [x]
  (print (format "x=%s\n" x))
  (+ x x))

(defn plus [x y] (+ x y))
(def memoized-dbl (memoize dbl))

(memoized-dbl 20)


