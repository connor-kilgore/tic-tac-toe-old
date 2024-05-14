(ns tic-tac-toe.turn-system
  (:require [tic-tac-toe.tic-tac-toe-board :as tttb]
            [tic-tac-toe.ui-dispatch-interface :as ui]
            [tic-tac-toe.ai-player :as ai]))

(defn is-turn? [user-symbol round]
  (or (and (= user-symbol 1) (odd? round))
      (and (= user-symbol 2) (even? round))))

(defn get-current-player [players round]
  (if (is-turn? (second (first players)) round) (first players) (second players)))

(defn is-ai? [player]
  (= (subs (first player) 0 2) "AI"))

(defn has-ai? [players]
  (some true? (map #(is-ai? %) players)))

(defn play-next-turn [game]
  (let [current-player (get-current-player (:players game) (:round game))]
    (if (is-ai? current-player)
      (tttb/increment-round (assoc game :board (ai/play-turn game (second current-player))))
      (ui/make-move {:ui     (:ui game) :game game
                     :symbol (second current-player)}))))
