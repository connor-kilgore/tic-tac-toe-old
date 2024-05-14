(ns tic-tac-toe.turn-system-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.ai-player :as ai]
            [tic-tac-toe.tic-tac-toe-board :as tttb]
            [tic-tac-toe.turn-system :refer :all]
            [tic-tac-toe.ui-dispatch-interface :as ui]))

(def base-game
  {:players {"Player" 1, "AI" 2},
   :difficulty 10, :round 1, :three-d false,
   :board [0 0 0 0 0 0 0 0 0], :ui :tui,
   :save-location :edndb, :parameters ()})

(describe "Turn System"

  (context "checks if ai is"
    (it "the player being checked."
      (should (is-ai? ["AI 1" 1]))
      (should-not (is-ai? ["Player 1" 1])))

    (it "within a list of players"
      (should-not (has-ai? [["Player" 1] ["Player 2" 2]]))
      (should (has-ai? [["Player" 1] ["AI" 2]]))))

  (context "Play's the"
    (it "ai's turn"
      (with-redefs [ai/play-turn (fn [_ _] "AI")
                    println (fn [_] nil)
                    tttb/increment-round (fn [x] x)]
        (should= "AI" (:board (play-next-turn (assoc base-game :round 2))))))
    (it "player's turn"
      (with-redefs [ui/make-move (fn [_] "Player")
                    println (fn [_] nil)]
        (should= "Player" (play-next-turn base-game)))))

  (context "checks if it is players turn by"
    (it "returning true if it is"
      (should (is-turn? 1 1))
      (should (is-turn? 2 2)))
    (it "returning false if it is not"
      (should-not (is-turn? 1 2))
      (should-not (is-turn? 2 1)))

    (it "returning the player whose turn it is"
      (should= [:player1 1] (get-current-player {:player1 1 :ai 1} 1))
      (should= [:ai 1] (get-current-player {:player1 1 :ai 1} 2)))))
