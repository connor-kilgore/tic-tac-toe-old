(ns tic-tac-toe.game-runner-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.game-runner :refer :all]
            [tic-tac-toe.save-interface :as save]
            [tic-tac-toe.turn-system :as turn]
            [tic-tac-toe.ui-dispatch-interface :as ui]))

(def base-initialized-game
  {:players    {"Player" 1, "AI" 2},
   :difficulty 10, :round 1,
   :three-d    false, :board [0 0 0 0 0 0 0 0 0],
   :ui         :tui, :save-location :edndb, :parameters ()})

(describe "Game Runner Module"

  (it "makes new match data"
    (should= {:players    {:player-1 1 :player-2 2}
              :difficulty 10
              :round      1
              :three-d    false
              :board      [0 0 0 0 0 0 0 0 0]} (reset-game
                                                 {:players    {:player-1 1 :player-2 2}
                                                  :difficulty 10
                                                  :round      3
                                                  :three-d    false
                                                  :board      [1 1 1 0 0 0 0 0 0]})))

  (context "gives an end condition string by"
    (it "saying you tied when no winner"
      (should= "\nTie!"
               (get-end-condition-string nil {"Player 1" 1 "AI" 2})))
    (it "saying who won if there is a winner"
      (should= "\nAI wins!"
               (get-end-condition-string 2 {"Player 1" 1 "AI" 2}))
      (should= "\nPlayer 1 wins!"
               (get-end-condition-string 1 {"Player 1" 1 "AI" 2}))))

  (context "runs the game-loop"
    (it "until a tie"
      (with-redefs [save/save-game! (fn [_] nil)
                    ui/round-output (fn [_] nil)
                    turn/play-next-turn (fn [_] nil)]
        (should= nil (game-loop base-initialized-game))))

    (it "until a win"
      (with-redefs [save/save-game! (fn [_] nil)
                    ui/round-output (fn [_] nil)
                    turn/play-next-turn (fn [_] nil)]
        (should= 1 (game-loop (-> base-initialized-game
                                  (assoc :board [1 1 1 0 0 0 0 0 0])))))))
  (context "parses arguments to set the"

    (it "valid arguments"
      (should= :gui (get-valid-arg ["--gui"] ui-types))
      (should= :archive (get-valid-arg ["--archive" "--gui"] ui-types))
      (should= :tui (get-valid-arg ["--not-a-real-arg"] ui-types)))

    (it "valid parameters"
      (should= () (get-valid-parameters nil))
      (should= () (get-valid-parameters ["--gui"]))
      (should= ["test.txt"] (get-valid-parameters ["test.txt"]))
      (should= ["test.txt"] (get-valid-parameters ["--archive" "test.txt"])))

    (it "ui"
      (should= (-> base-initialized-game
                   (assoc :ui :gui)
                   (assoc :save-location :edndb))
               (parse-arguments base-initialized-game ["--gui"])))
    (it "save-location"
      (should= (-> base-initialized-game
                   (assoc :ui :tui)
                   (assoc :save-location :psqldb))
               (parse-arguments base-initialized-game ["--psqldb"])))

    (it "archive"
      (should= (-> base-initialized-game
                   (assoc :ui :archive)
                   (assoc :save-location :edndb)
                   (assoc :parameters ["test.txt"]))
               (parse-arguments base-initialized-game ["--archive" "test.txt"])))

    (it "any arguments"
      (should= (-> base-initialized-game
                   (assoc :ui :gui)
                   (assoc :save-location :psqldb)
                   (assoc :parameters ["test.txt"]))
               (parse-arguments base-initialized-game ["--psqldb" "--gui" "--not-real" "--archive" "test.txt"]))))

  (context "plays a single round by"

    (it "saving the game first"
      (with-redefs [save/save-game! (fn [_] (print "saved!"))
                    ui/round-output (fn [_] nil)
                    turn/play-next-turn (fn [_] nil)]
        (should= "saved!"
                 (with-out-str (game-round base-initialized-game)))))

    (it "Then giving the round output"
      (with-redefs [save/save-game! (fn [_] nil)
                    turn/play-next-turn (fn [_] nil)]
        (should= (str "\n===  Player  === ROUND  1  ===\n\n 0| 1|"
                      " 2\n========\n 3| 4| 5\n========\n 6| 7| 8\n")
                 (with-out-str (game-round base-initialized-game)))))

    (it "Then returns a winner if there is one"
      (with-redefs [save/save-game! (fn [_] nil)
                    turn/play-next-turn (fn [_] nil)
                    ui/round-output (fn [_] nil)]
        (should= 1
                 (game-round (assoc base-initialized-game
                   :board [1 1 1 0 0 0 0 0 0])))))

    (it "Then returns nil if there are no moves left"
      (with-redefs [save/save-game! (fn [_] nil)
                    read-line (fn [] "0")
                    ui/round-output (fn [_] nil)]
        (should= nil
                 (game-round (assoc base-initialized-game :round 10)))))

    (it "finally returning a new game state on
    the next round with a play made"
      (with-redefs [read-line (fn [] "0")
                    println (fn [& _] nil)]
        (should= [1 0 0 0 0 0 0 0 0]
                 (:board (game-round base-initialized-game)))))
    )
  )
