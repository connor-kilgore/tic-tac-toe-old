(ns tic-tac-toe.archive
  (:require [tic-tac-toe.ui-dispatch-interface :as ui]
            [tic-tac-toe.save-interface :as save]))

(defn get-current-player [turn-state]
  (if (odd? (:round turn-state))
    (first (:players turn-state))
    (second (:players turn-state))))

(defn turn-to-str [turn-state]
    (let [current-player (get-current-player turn-state)]
      (println
        "\n=== ROUND: " (:round turn-state)
        " === " (first current-player) " ==="
        (ui/get-tttb-string turn-state))))

(defn game-to-str [game-state]
    (doseq [turn-state game-state] (turn-to-str turn-state)))

(defn print-archived-game [game]
    (let [game-state (save/get-game-archive game)]
      (if (nil? game-state) (println "Game not found... Goodbye.")
                            (game-to-str game-state))))

(defmethod ui/initialize-game :archive [game]
  (print-archived-game game)
  :close-program)



(defmethod ui/initialize-ui :archive [game]
  (println "ARCHIVE OF GAME" (first (:parameters game))))

