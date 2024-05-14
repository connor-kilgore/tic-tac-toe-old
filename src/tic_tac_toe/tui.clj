(ns tic-tac-toe.tui
  (:require [tic-tac-toe.ui-dispatch-interface :as ui]
            [tic-tac-toe.option-selector :as menu]
            [tic-tac-toe.tic-tac-toe-board :as tttb]))

(defmethod ui/print-text :tui [text]
  (println (:text text)))

(defn get-current-player [game]
  (if (= (mod (:round game) 2) 0)
    (second (:players game))
    (first (:players game))))

(defmethod ui/round-output :tui [game]
  (println "\n=== " (first (get-current-player game)) " === ROUND " (:round game) " ===")
  (println (ui/get-tttb-string game)))

(defmethod ui/get-selection :tui [options]
  (println (:print-statement (:options options)))
  (let [selection (get (:options options) (read-line))]
    (if (nil? selection)
      (do (println (:error (:options options))) (recur options))
      selection)))

(defn get-players [selection game]
  (cond (= selection :one-player) (ui/get-selection {:options menu/symbol-options :ui (:ui game)})
        (= selection :close-program) nil
        :else selection))

(defn get-difficulty [players game]
  (if (or (nil? players) (not (:has-ai players)))
    nil
    (ui/get-selection {:options menu/difficulty-options :ui (:ui game)})))

(defn get-dimensions [selection game]
  (if (= selection :close-program)
    nil
    (ui/get-selection {:options menu/board-size-options :ui (:ui game)})))

(defmethod ui/initialize-game :tui [game]
  (let [selection (ui/get-selection {:options menu/menu-options :ui (:ui game)})
        players (get-players selection game)
        difficulty (get-difficulty players game)
        dimensions (get-dimensions selection game)]
    (if (= selection :close-program)
      selection
      (-> game
          (assoc :players (:players players))
          (assoc :difficulty difficulty)
          (assoc :three-d (:three-d dimensions))
          (assoc :board (tttb/make-board dimensions))))))

(def spot-taken-text "\nSpace already taken, try again.")

(defmethod ui/make-move :tui [options]
  (let [game (:game options)
        position (ui/get-selection
                   {:options (get menu/move-options (count (:board game)))
                    :ui      (:ui game)})]
    (if (tttb/spot-available? (:board game) position)
      (tttb/increment-round (assoc game
                              :board (tttb/place-value-into-tttb
                                       (:board game) (:symbol options) position)))
      (do (ui/print-text {:text spot-taken-text :ui :tui}) (recur game)))))

(defmethod ui/end-game :tui [end-condition-str]
  (println (:end-cond end-condition-str))
  (ui/get-selection {:options menu/retry-options :ui :tui}))

(defmethod ui/close-program :tui [_]
  (println "\nClosing program. Goodbye."))

(defmethod ui/initialize-ui :tui [_]
  (println "\nWelcome to the Unbeatable Tic-Tac-Toe Game!"))
