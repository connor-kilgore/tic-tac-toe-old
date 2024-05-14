(ns tic-tac-toe.ai-player-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.ai-player :refer :all]))

(describe "AI Player"

  (context "Makes all possible moves on a given board"
    (it "returns empty vec if no plays left"
      (should= {} (make-hypothetical-moves [1 1 1 1 1 1 1 1 1] 1)))

    (it "1 board with position played if 1 play left"
      (should= {6 [1 1 1 1 1 1 2 1 1]} (make-hypothetical-moves [1 1 1 1 1 1 0 1 1] 2)))

    (it "a series of possible plays if more than 1 play left"
      (should= {8 [0 0 0 0 0 0 0 0 1]
                7 [0 0 0 0 0 0 0 1 0]
                6 [0 0 0 0 0 0 1 0 0]
                5 [0 0 0 0 0 1 0 0 0]
                4 [0 0 0 0 1 0 0 0 0]
                3 [0 0 0 1 0 0 0 0 0]
                2 [0 0 1 0 0 0 0 0 0]
                1 [0 1 0 0 0 0 0 0 0]
                0 [1 0 0 0 0 0 0 0 0]} (make-hypothetical-moves [0 0 0 0 0 0 0 0 0] 1))))

  (context "gets score of end condition"
    (it "returns 0 if a tie"
      (should= 0 (get-end-score {:board [1 2 1 2 2 1 1 1 2] :three-d false} 1)))

    (it "returns 1 if ai wins at depth 9"
      (should= 1 (get-end-score {:board [1 1 1 2 1 2 1 2 2] :three-d false} 1)))

    (it "returns -1 if ai loses at depth 9"
      (should= -1 (get-end-score {:board [1 1 1 2 1 2 1 2 2] :three-d false} 2))))

  (context "checks if it's currently the ai's turn or not"
    (it "returns false if depth is not even"
      (should (is-ai-turn? 0)))
    (it "returns true if depth is even"
      (should-not (is-ai-turn? 1))))

  (context "gives the right symbol depending on the context of
                 the ai's symbol and the depth"
    (it "returns 1 if ai symbol is 1 on first move"
      (should= 1 (get-current-symbol 0 1)))
    (it "returns 2 if ai symbol is 1 on the second move"
      (should= 2 (get-current-symbol 1 1))))

  (context "makes the best possible move based on an algorithm"
    (it "moves to any space when absolute depth is below 5"
      (should= [1 0 0 0 0 0 0 0 0] (mini-max-algo {:board [0 0 0 0 0 0 0 0 0] :three-d false} 1))
      (should= [1 0 0 0 2 0 0 0 0] (mini-max-algo {:board [1 0 0 0 0 0 0 0 0] :three-d false} 2)))


    (it "actively blocks player movement until tie or ai wins"
      (should= [1 2 1 0 2 2 2 1 1] (mini-max-algo {:board [1 2 1 0 2 0 2 1 1] :three-d false} 2))
      (should= [1 2 1 0 2 0 0 0 0] (mini-max-algo {:board [1 0 1 0 2 0 0 0 0] :three-d false} 2))
      (should= [1 2 1 0 2 0 0 2 1] (mini-max-algo {:board [1 2 1 0 2 0 0 0 1] :three-d false} 2))
      (should= [1 2 1 2 2 0 0 1 0] (mini-max-algo {:board [1 2 1 0 2 0 0 1 0] :three-d false} 2))
      (should= [1 2 1 2 2 1 0 1 2] (mini-max-algo {:board [1 2 1 2 2 1 0 1 0] :three-d false} 2)))

    (it "works for 4x4 too"
      (should= [0 2 0 0 0 1 0 0 0 1 2 0 0 1 0 2]
               (mini-max-algo {:board [0 0 0 0 0 1 0 0 0 1 2 0 0 1 0 2] :three-d false} 2)))

    (it "and 3x3x3"
      (should= [1 0 0 0 0 0 0 0 0
                0 1 0 0 0 0 0 0 0
                0 0 2 0 0 0 0 0 0] (mini-max-algo {:board [1 0 0 0 0 0 0 0 0
                                                           0 1 0 0 0 0 0 0 0
                                                           0 0 0 0 0 0 0 0 0] :three-d true} 2)))
    )


  (context "gives the best best score for the algorithm"
    (it "the biggest score if it's the ai's turn"
      (should= [3 4] (get-best-score 0 {1 2 3 4}))
      (should= [3 4] (get-best-score 0 {3 4 1 2})))
    (it "the smallest score if not the ai's turn"
      (should= [1 2] (get-best-score 1 {1 2 3 4}))
      (should= [1 2] (get-best-score 1 {3 4 1 2}))))
  )

