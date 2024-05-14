(ns tic-tac-toe.ui-dispatch-interface
  (:require [tic-tac-toe.symbols :as symbols]))

(defmulti initialize-ui :ui)
(defmulti initialize-game :ui)
(defmulti round-output :ui)
(defmulti make-move :ui)
(defmulti print-text :ui)
(defmulti end-game :ui)
(defmulti get-selection :ui)
(defmulti close-program :ui)
(defn replace-0-with-index [index val]
  (if (= val 0) (format "%2d" index) val))

(defn convert-board-vals-to-symbols [board]
  (->> board (map symbols/symbols)
       (map-indexed replace-0-with-index)
       (partition (int (Math/sqrt (count board))))))

(defn convert-three-d-board-vals-to-symbols [board]
  (->> board (map symbols/symbols)
       (map-indexed replace-0-with-index)
       (partition (int (Math/pow (count board) (/ 1 3))))))

(defn add-vertical-dividers [board]
  (map #(interpose "|" %) board))

(def horizontal-divider-count
  {9  8
   16 11})

(defn add-horizontal-dividers [board board-str]
  (->> board-str (interpose
                   (str "\n" (apply str (repeat (get horizontal-divider-count
                                                     (count board)) "=")) "\n"))))
(defmulti get-tttb-string :three-d)

(defmethod get-tttb-string true [game]
  (let [boards (->> (flatten (:board game))
                    (convert-three-d-board-vals-to-symbols)
                    (partition (int (Math/pow (count (flatten (:board game))) (/ 1 3)))))]
    (->> (for [board boards] (get-tttb-string {:board (flatten board) :three-d false} board))
         (interpose "\n") (apply str))))

(defmethod get-tttb-string false
  ([game] (get-tttb-string game (convert-board-vals-to-symbols (:board game))))
  ([game board]
   (str "\n" (->> board
                  (add-vertical-dividers)
                  (add-horizontal-dividers (:board game))
                  (apply concat) (apply str)))))