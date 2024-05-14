(ns tic-tac-toe.game-runner
  (:gen-class)
  (:require [tic-tac-toe.tic-tac-toe-board :as tttb]
            [tic-tac-toe.turn-system :as turn]
            [tic-tac-toe.win-checker :as win?]
            [tic-tac-toe.ui-dispatch-interface :as ui]
            [tic-tac-toe.save-interface :as save]
            [tic-tac-toe.tui]
            [tic-tac-toe.gui]
            [tic-tac-toe.edndb]
            [tic-tac-toe.psqldb]
            [tic-tac-toe.archive]))

(defn get-end-condition-string [winner players]
  (cond (nil? winner) "\nTie!"
        (= (second (first players)) winner) (str "\n" (first (first players)) " wins!")
        :else (str "\n" (first (second players)) " wins!")))

(def base-game
  {:players       nil
   :difficulty    nil
   :round         1
   :three-d?      nil
   :board         nil
   :ui            nil
   :save-location nil
   :parameters    []})

(defn reset-game [game]
  (-> game
      (assoc :round 1)
      (assoc :board (tttb/make-board {:size    (tttb/get-side-len game)
                                      :three-d (:three-d game)}))))

(defn game-round [game]
  (save/save-game! game)
  (ui/round-output game)
  (let [winner (win?/get-winner game)]
    (cond (or (not (nil? winner))
              (> (:round game) (count (:board game)))) winner
          :else (turn/play-next-turn game))))

(defn game-loop [game]
  (let [game-result (game-round game)]
    (if (map? game-result)
    (recur game-result)
    game-result)))

(defn run-game-loop [game]
  (let [winner (game-loop game)]
    (save/archive-game game)
    (let [end-cond (get-end-condition-string winner (:players game))
          retry? (ui/end-game {:end-cond end-cond :ui (:ui game)})]
      (when retry? (recur (reset-game game))))))

(def resume-options
  {:print-statement "\nPrevious game detected. Would you like to resume? \n[1] Yes\n[2] No\n"
   :error           "\nPlease select a valid option."
   "1"              true
   "2"              false})

(defn get-new-game [game last-game]
  (ui/initialize-ui game)
  (if (and (not (empty? last-game)) (ui/get-selection {:options resume-options :ui (:ui game)}))
    (-> (last last-game)
        (assoc :ui (:ui game))
        (assoc :save-location (:save-location game))
        (assoc :parameters (:parameters game)))
    (ui/initialize-game game)))

(defn menu-loop [game]
  (let [last-game (save/get-last-game game)
        initialized-game (get-new-game game last-game)]
    (if (= initialized-game :close-program)
      (ui/close-program game)
      (do
        (run-game-loop initialized-game)
        (recur game)))))

(def ui-types
  {"--gui"     :gui
   "--archive" :archive
   :default    :tui})

(def save-locations
  {"--psqldb" :psqldb
   :default   :edndb})

(defn get-valid-arg [args arg-map]
  (let [valid-arg (first (filter #(not (nil? %)) (for [arg args] (get arg-map arg))))]
    (if (nil? valid-arg)
      (:default arg-map)
      valid-arg)))

(defn is-flag? [arg]
  (= (first arg) (second arg) (first "-")))

(defn get-valid-parameters [args]
  (filter #(not (is-flag? %)) args))

(defn parse-arguments [game args]
  (-> game
      (assoc :ui (get-valid-arg args ui-types))
      (assoc :save-location (get-valid-arg args save-locations))
      (assoc :parameters (get-valid-parameters args))))

(defn -main [& args]
  (let [new-game (parse-arguments base-game args)]
    (menu-loop new-game)))
