(ns tic-tac-toe.win-checker)

(defn get-rows [board]
  (->> board (partition (int (Math/sqrt (count board))))))

(defn get-cols [board]
  (->> (map-indexed vector board)
       (group-by #(mod (first %) (int (Math/sqrt (count board)))))
       (map second)
       (map (fn [inner-map] (map #(second %) inner-map)))))

(defn get-diagonals [board]
  (let [rows (get-rows board)]
    (list (for [[i v] (map-indexed vector rows)] (nth v i))
          (for [[i v] (map-indexed vector rows)] (nth v (- (dec (count v)) i))))))

(defn get-horizontal-winner [board]
  (->> (get-rows board) (filter #(and (not (= (apply + %) 0))
                                      (apply = %))) (first) (first)))

(defn get-vertical-winner [board]
  (->> (get-cols board) (filter #(and (not (= (apply + %) 0))
                                      (apply = %))) (first) (first)))

(defn get-diagonal-winner [board]
  (->> (get-diagonals board) (filter #(and (not (= (apply + %) 0))
                                           (apply = %))) (first) (first)))

(defn get-down-vertical-boards [three-d-board]
  (let [side-size (int (Math/pow (count three-d-board) (/ 1 3)))]
    (for [i (range side-size)]
      (map #(nth three-d-board %)
           (filter #(= (mod % side-size) i) (range (count three-d-board)))))))

(defn get-side-vertical-boards [three-d-board]
  (let [side-size (int (Math/pow (count three-d-board) (/ 1 3)))]
    (for [i (range side-size)]
      (map #(nth three-d-board %)
           (filter #(= (mod (quot % side-size) side-size) i) (range (count three-d-board)))))))

(defn get-diagonal-facing-horizontal-boards [three-d-board]
  (let [side-size (int (Math/pow (count three-d-board) (/ 1 3)))
        two-d-size (int (Math/pow (count three-d-board) (/ 2 3)))]
    (list
      (for [i (range two-d-size)]
        (nth three-d-board (+ i (* (quot i side-size) two-d-size))))

      (for [i (range two-d-size)]
        (nth three-d-board (- (+ (+ (- two-d-size side-size) (mod i side-size))
                                 (* two-d-size (quot i side-size)))
                              (* side-size (quot i side-size))))))))

(defn get-diagonal-facing-vertical-boards [three-d-board]
  (let [side-size (int (Math/pow (count three-d-board) (/ 1 3)))
        two-d-size (int (Math/pow (count three-d-board) (/ 2 3)))]
    (list
      (for [i (range two-d-size)]
        (nth three-d-board (+ (+ (mod i side-size) (* side-size (mod i side-size)))
                              (* two-d-size (quot i side-size)))))

      (for [i (range two-d-size)]
        (nth three-d-board (+ (+ (- (dec side-size) (mod i side-size))
                                 (* side-size (mod i side-size)))
                              (* two-d-size (quot i side-size))))))))

(defn get-horizontal-boards [three-d-board]
  (let [two-d-size (int (Math/pow (count three-d-board) (/ 2 3)))]
    (partition two-d-size three-d-board)))

(defn get-2d-boards-within-3d-board [three-d-board]
  (concat
    (get-horizontal-boards three-d-board)
    (get-diagonal-facing-horizontal-boards three-d-board)
    (get-diagonal-facing-vertical-boards three-d-board)
    (get-side-vertical-boards three-d-board)
    (get-down-vertical-boards three-d-board)))

(defmulti get-winner :three-d)

(defmethod get-winner false [board]
  (first (filter #(not (nil? %)) [(get-vertical-winner (:board board))
                                  (get-horizontal-winner (:board board))
                                  (get-diagonal-winner (:board board))])))

(defmethod get-winner true [board]
  (first (filter #(not (nil? %))
                 (for [two-d-board (get-2d-boards-within-3d-board (:board board))]
                   (get-winner {:board two-d-board :three-d false})))))

(defn get-winner-or-tie [game]
  (let [winner (get-winner game)]
    (cond (not (nil? winner)) winner
          (empty? (filter zero? (:board game))) "tie"
          :else nil)))