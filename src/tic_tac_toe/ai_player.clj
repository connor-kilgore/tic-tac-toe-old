(ns tic-tac-toe.ai-player
  (:require [tic-tac-toe.tic-tac-toe-board :as tttb]
            [tic-tac-toe.symbols :as symbols]
            [tic-tac-toe.win-checker :as win?]))

(def depth-limit
  {9  5
   16 6
   27 3})

(defn below-depth-limit? [board]
  (< (tttb/get-absolute-depth board) (get depth-limit (count board))))

(defn is-ai-turn? [depth] (even? depth))

(defn sleep-n-seconds [n]
  (Thread/sleep (* n 1000)))

(defn get-current-symbol [depth symbol]
  (if (is-ai-turn? depth) symbol (symbols/reverse-symbol symbol)))

(defn get-end-score [game ai-symbol]
  (let [end-condition (win?/get-winner-or-tie game)
        depth (tttb/get-absolute-depth (:board game))]
    (cond (= end-condition "tie") 0
          (nil? end-condition) nil
          (not (= end-condition ai-symbol)) (- depth (inc (count (:board game))))
          :else (- (inc (count (:board game))) depth))))

(defn make-hypothetical-moves
  ([board symbol] (make-hypothetical-moves board symbol 0 {}))
  ([board symbol position new-boards]
   (cond (>= position (count board)) new-boards
         (tttb/spot-available? board position)
         (recur board symbol
                (inc position)
                (conj {position (tttb/place-value-into-tttb board symbol position)} new-boards))
         :else (recur board symbol (inc position) new-boards))))

(defn get-best-score [depth scores]
  (if (is-ai-turn? depth)
    (apply max-key val scores)
    (apply min-key val scores)))

(defn get-new-alpha [score alpha depth]
  (if (and (is-ai-turn? depth) (not (empty? score)))
    (max (second score) alpha)
    alpha))

(defn get-new-beta [score beta depth]
  (if (or (is-ai-turn? depth) (empty? score))
    beta
    (min (second score) beta)))

(defn get-new-score [score-1 score-2 depth]
  (cond (empty? score-1) score-2
        (empty? score-2) score-1
        (is-ai-turn? depth) (apply max-key second [score-2 score-1])
        :else (apply min-key second [score-2 score-1])))

(defn get-depth-score [depth]
  (if (is-ai-turn? depth)
    depth
    (* depth -1)))

(defn mini-max-algo
  ([game symbol]
   (mini-max-algo
     game symbol 0
     (-> (:board game) (count) (inc) (* -1)) (-> (:board game) (count) (inc))))
  ([game symbol depth alpha beta]
   (let [end-condition (get-end-score game symbol)]
     (if (not (nil? end-condition))
       end-condition
       (let [moves (make-hypothetical-moves (:board game) (get-current-symbol depth symbol))]

         (loop [i 0
                a alpha
                b beta
                best-score []]

           (cond (> depth (get depth-limit (count (:board game)))) (get-depth-score depth)
                 (and (= depth 0) (>= i (count (:board game)))) (get moves (first best-score))
                 (>= i (count (:board game))) (second best-score)
                 (nil? (get moves i)) (recur (inc i) a b best-score)
                 (<= b a) (recur (inc i) a b best-score)
                 :else (let [score [i (mini-max-algo (assoc game :board (get moves i)) symbol (inc depth) a b)]
                             new-score (get-new-score best-score score depth)
                             new-alpha (get-new-alpha score a depth)
                             new-beta (get-new-beta score b depth)]
                         (recur (inc i) new-alpha new-beta new-score)
                         ))))))))

(defn place-random-spot [board symbol]
  (let [position (rand-int (count board))]
    (if (tttb/spot-available? board position)
      (tttb/place-value-into-tttb board symbol position)
      (recur board symbol))))

(defmulti play-best-turn :three-d)

(defmethod play-best-turn false [turn-state]
  (mini-max-algo (:game turn-state) (:symbol turn-state)))

(defmethod play-best-turn true [turn-state]
  (if (= (:round (:game turn-state)) 1)
    (tttb/place-value-into-tttb (:board (:game turn-state))
                                (:symbol turn-state)
                                (quot (count (:board (:game turn-state))) 2))
    (mini-max-algo (:game turn-state) (:symbol turn-state))))

(defn play-turn [game ai-symbol]
  (if (> (rand-int 11) (:difficulty game))
    (place-random-spot (:board game) ai-symbol)
    (play-best-turn {:three-d (:three-d game) :game game :symbol ai-symbol})))
