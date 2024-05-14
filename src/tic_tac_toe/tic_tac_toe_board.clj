(ns tic-tac-toe.tic-tac-toe-board)

(defmulti get-side-len :three-d)
(defmethod get-side-len true [game]
  (int (Math/pow (count (:board game)) (/ 1 3))))
(defmethod get-side-len false [game]
  (int (Math/sqrt (count (:board game)))))

(defmulti make-board :three-d)

(defmethod make-board true [size]
  (into [] (repeat (* (:size size) (:size size) (:size size)) 0)))

(defmethod make-board false [size]
  (into [] (repeat (* (:size size) (:size size)) 0)))

(defn spot-available? [board position]
  (= (get board position) 0))

(defn place-value-into-tttb [board value position]
  (if (not (spot-available? board position))
    board
    (assoc board position value)))

(defn increment-round [game]
  (assoc game :round (inc (:round game))))

(defn not-zero? [x]
  (not (zero? x)))

(defn get-absolute-depth [board]
  (count board) (->> board (filter not-zero?) (count)))