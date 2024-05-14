(ns tic-tac-toe.win-checker-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.win-checker :refer :all]))

(describe "Win Checker"
  (context "gets all 2D boards within a 3D board"
    (it "down-facing vertical boards"
      (should= [[0 3 6 9 12 15 18 21 24]
                [1 4 7 10 13 16 19 22 25]
                [2 5 8 11 14 17 20 23 26]] (get-down-vertical-boards (range 27)))

      (should= [[0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0]]
               (get-down-vertical-boards (repeat 27 0))))

    (it "side-facing vertical boards"
      (should= [[0 1 2 9 10 11 18 19 20]
                [3 4 5 12 13 14 21 22 23]
                [6 7 8 15 16 17 24 25 26]] (get-side-vertical-boards (range 27)))

      (should= [[0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0]]
               (get-side-vertical-boards (repeat 27 0))))

    (it "diagonal-facing vertical boards"
      (should= [[0 4 8 9 13 17 18 22 26] [2 4 6 11 13 15 20 22 24]]
               (get-diagonal-facing-vertical-boards (range 27)))

      (should= [[0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0]]
               (get-diagonal-facing-vertical-boards (repeat 27 0))))

    (it "diagonal-facing horizontal boards"
      (should= [[0 1 2 12 13 14 24 25 26] [6 7 8 12 13 14 18 19 20]]
               (get-diagonal-facing-horizontal-boards (range 27)))

      (should= [[0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0]]
               (get-diagonal-facing-horizontal-boards (repeat 27 0))))

    (it "horizontal boards"
      (should= [[0 1 2 3 4 5 6 7 8]
                [9 10 11 12 13 14 15 16 17]
                [18 19 20 21 22 23 24 25 26]] (get-horizontal-boards (range 27)))

      (should= [[0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0]]
               (get-horizontal-boards (repeat 27 0))))

    (it "all together"
      (should= [[0 1 2 3 4 5 6 7 8] [9 10 11 12 13 14 15 16 17]
                [18 19 20 21 22 23 24 25 26] [0 1 2 12 13 14 24 25 26]
                [6 7 8 12 13 14 18 19 20] [0 4 8 9 13 17 18 22 26]
                [2 4 6 11 13 15 20 22 24] [0 1 2 9 10 11 18 19 20]
                [3 4 5 12 13 14 21 22 23] [6 7 8 15 16 17 24 25 26]
                [0 3 6 9 12 15 18 21 24] [1 4 7 10 13 16 19 22 25]
                [2 5 8 11 14 17 20 23 26]] (get-2d-boards-within-3d-board (range 27)))

      (should= [[0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0]
                [0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0]
                [0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0]
                [0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0] [0 0 0 0 0 0 0 0 0]
                [0 0 0 0 0 0 0 0 0]]
               (get-2d-boards-within-3d-board (repeat 27 0)))))

  (context "checks if a winner within a 3-d board is found,"
    (it "nil on no winner"
      (should= nil (get-winner {:three-d true :board (repeat 27 0)})))
    (it "flat-horizontal win"
      (should= 1 (get-winner {:three-d true :board [1 1 1 0 0 0 0 0 0
                                                    0 0 0 0 0 0 0 0 0
                                                    0 0 0 0 0 0 0 0 0]})))
    (it "flat-vertical win"
      (should= 1 (get-winner {:three-d true :board [1 0 0 1 0 0 1 0 0
                                                    0 0 0 0 0 0 0 0 0
                                                    0 0 0 0 0 0 0 0 0]})))
    (it "flat-diagonal win"
      (should= 1 (get-winner {:three-d true :board [1 0 0 0 1 0 0 0 1
                                                    0 0 0 0 0 0 0 0 0
                                                    0 0 0 0 0 0 0 0 0]})))
    (it "upwards-vertical win"
      (should= 1 (get-winner {:three-d true :board [1 0 0 0 0 0 0 0 0
                                                    1 0 0 0 0 0 0 0 0
                                                    1 0 0 0 0 0 0 0 0]})))
    (it "upwards-diagonal win"
      (should= 1 (get-winner {:three-d true :board [1 0 0 0 0 0 0 0 0
                                                    0 1 0 0 0 0 0 0 0
                                                    0 0 1 0 0 0 0 0 0]})))
    (it "cross-diagonal win"
      (should= 1 (get-winner {:three-d true :board [1 0 0 0 0 0 0 0 0
                                                    0 0 0 0 1 0 0 0 0
                                                    0 0 0 0 0 0 0 0 1]}))
      (should= 1 (get-winner {:three-d true :board [0 0 0 0 0 0 0 0 1
                                                    0 0 0 0 1 0 0 0 0
                                                    1 0 0 0 0 0 0 0 0]}))))

  (context "checks if the game has met an end condition"
    (it "returns symbol of winner if a horizontal win is found"
      (should= 1 (get-horizontal-winner [1 1 1 0 0 0 0 0 0]))
      (should= 2 (get-horizontal-winner [1 1 2 0 0 0 2 2 2])))
    (it "returns nil if no horizontal win is found"
      (should= nil (get-horizontal-winner [1 1 2 0 0 0 0 0 0])))

    (it "returns a symbol of winner if a vertical win is found"
      (should= 1 (get-vertical-winner [1 0 0 1 0 0 1 0 0]))
      (should= 2 (get-vertical-winner [1 2 0 2 2 0 1 2 0])))
    (it "returns nil if no vertical win is found"
      (should= nil (get-vertical-winner [1 0 0 1 0 0 2 0 0])))

    (it "returns a symbol of winner if a diagonal win is found"
      (should= 1 (get-diagonal-winner [1 0 0 0 1 0 0 0 1]))
      (should= 2 (get-diagonal-winner [1 0 2 0 2 0 2 0 1])))
    (it "returns nil if no diagonal win is found"
      (should= nil (get-diagonal-winner [1 1 1 0 2 2 2 2 1])))

    (it "returns symbol of winner if a winner is found"
      (should= 1 (get-winner {:board [1 0 0 1 0 0 1 0 0] :three-d false}))
      (should= 2 (get-winner {:board [2 2 2 1 0 2 1 0 0] :three-d false}))
      (should= 1 (get-winner {:board [1 2 2 1 0 2 1 0 1] :three-d false})))
    (it "returns nil if no winner found"
      (should= nil (get-winner {:board [0 0 0 0 0 0 0 0 0] :three-d false})))

    (it "checks if there is a winner or a tie"
      (should= "tie" (get-winner-or-tie {:board [1 2 1 1 2 2 2 1 1] :three-d false}))
      (should= 1 (get-winner-or-tie {:board [1 1 1 1 2 2 2 1 1] :three-d false}))
      (should= nil (get-winner-or-tie {:board [1 1 0 1 2 2 2 1 1] :three-d false})))))