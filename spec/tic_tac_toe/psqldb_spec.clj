(ns tic-tac-toe.psqldb-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.psqldb :refer :all]
            [tic-tac-toe.save-interface :as save]))

(def game-state {:players    {"Player" 1, "AI" 2},
                 :difficulty 10, :round 1,
                 :three-d    false, :board [0 1 2 1 2 0 2 0 1],
                 :ui         :tui, :save-location :psqldb,
                 :parameters ()})

(def base-game {:players    nil,
                :difficulty nil, :round nil,
                :three-d    nil, :board nil,
                :ui         :tui, :save-location :psqldb,
                :parameters ()})
(def test-games-entry
  {:file       "test",
   :players    "{\"Player\" 1, \"AI\" 2}",
   :difficulty 10, :three_d false})

(describe "PostgreSQL database"

  (context "can query for games entries from the db by returning"
    (it "the game if it exists"
      (should= test-games-entry (get-game-from-games-table "test")))
    (it "nil if it doesn't"
      (should= nil (get-game-from-games-table "not-real"))))

  (context "converts a value into a map suitable to be inserted into the db such as a"
    (it "board"
      (should= [{:file "test", :index 0, :round 2, :value 0}
                {:file "test", :index 1, :round 2, :value 1}
                {:file "test", :index 2, :round 2, :value 2}]
               (get-board-entries-map {:board [0 1 2] :round 2} "test")))

    (it "game"
      (should= {:file       "test", :players "{\"Player\" 1, \"AI\" 2}",
                :difficulty 10, :three_d false} (get-game-row game-state "test"))))

  (context "saves a value to the database if not duplicate, such as a"
    (it "board"
      (let [return-val (save-board-to-db game-state "test")]
        (should (or (= return-val [{:file "test", :index 0, :round 1, :value 0}
                                   {:file "test", :index 1, :round 1, :value 1}
                                   {:file "test", :index 2, :round 1, :value 2}
                                   {:file "test", :index 3, :round 1, :value 1}
                                   {:file "test", :index 4, :round 1, :value 2}
                                   {:file "test", :index 5, :round 1, :value 0}
                                   {:file "test", :index 6, :round 1, :value 2}
                                   {:file "test", :index 7, :round 1, :value 0}
                                   {:file "test", :index 8, :round 1, :value 1}])
                    (= return-val nil)))))
    (it "game"
      (let [return-val (first (save-game-to-db game-state "test"))]
        (should (or (= return-val (get-game-row game-state "test"))
                    (= return-val nil))))))

  (context "gets details of the game"
    (it "the round"
      (should= 1 (get-current-round-of-game "test")))
    (it "the size"
      (should= 9 (get-size-of-game "test")))
    (it "the board at a round"
      (should= [0 1 2 1 2 0 2 0 1] (get-board-of-game-at-round "test" 1)))
    (it "every board of every round"
      (should= [[0 1 2 1 2 0 2 0 1]] (get-boards-of-game "test"))))

  (it "saves the game onto last"
    (let [return-val (save/save-game! game-state)]
      (should (or (= return-val [{:file "last", :index 0, :round 1, :value 0}
                                 {:file "last", :index 1, :round 1, :value 1}
                                 {:file "last", :index 2, :round 1, :value 2}
                                 {:file "last", :index 3, :round 1, :value 1}
                                 {:file "last", :index 4, :round 1, :value 2}
                                 {:file "last", :index 5, :round 1, :value 0}
                                 {:file "last", :index 6, :round 1, :value 2}
                                 {:file "last", :index 7, :round 1, :value 0}
                                 {:file "last", :index 8, :round 1, :value 1}])
                  (= return-val nil)))))

  (it "gives the game details of the last game"
    (with-redefs [last-game-path "test"]
      (should= [{:players       {"Player" 1, "AI" 2}, :difficulty 10,
                 :round         1, :three-d false,
                 :board         [0 1 2 1 2 0 2 0 1], :ui :tui,
                 :save-location :psqldb, :parameters ()}]
               (save/get-last-game base-game))))

  (it "deletes a game from the db"
    (should= [9 1] (delete-game "last")))

  (it "archives a finished game"
    (with-redefs [save/get-save-name "test"
                  delete-game (fn [_] nil)]
      (should= nil (save/archive-game base-game))))

  )
