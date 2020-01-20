(defn neighbours [[x y]]
  (for [dx [-1 0 1] dy (if (zero? dx) [-1 1] [-1 0 1])]
    [(+ dx x) (+ dy y)]))

(defn step [cells]
  (set (for [[loc n] (frequencies (mapcat neighbours cells))
             :when (or (= n 3) (and (= n 2) (cells loc)))]
         loc)))

(def board #{[2 1] [2 2] [2 3]})

(defn print-board [board w h]
  (doseq [x (range (inc w)) y (range (inc h))]
    (if (= y 0) (print "\n")) 
    (print (if (board [x y]) "[X]" " . "))))

(defn display-grids [grids w h]
  (doseq [board grids]
    (print-board board w h)
    (print "\n")))
