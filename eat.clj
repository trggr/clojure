(defn eat [orig]
  (let [pos (list (:x orig) (:y orig))]
    (if (contains? @*plants* pos)
      (dosync (ref-set *plants* (disj @*plants* pos))
        (assoc orig :energy (+ (:energy orig) *plant-energy*)))
      orig)))


(defn eat [{:keys [x y energy] :as animal}]
  (let [pos (list x y)]
    (dosync
      (when (contains? @*plants* pos)
        (alter *plants* disj pos)
          (assoc animal :energy (+ energy *plant-energy*))))))