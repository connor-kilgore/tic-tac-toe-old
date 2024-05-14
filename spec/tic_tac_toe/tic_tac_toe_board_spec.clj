(ns tic-tac-toe.tic-tac-toe-board-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.tic-tac-toe-board :refer :all]))
(def empty-board
  [0 0 0
   0 0 0
   0 0 0])

(def mixed-board
  [1 0 2
   0 2 1
   2 1 0])

(describe "Tic Tac Toe Board"

  (it "gets the board side length"
    (should= 3 (get-side-len {:board (repeat 9 0) :three-d false}))
    (should= 4 (get-side-len {:board (repeat 16 0) :three-d false}))
    (should= 3 (get-side-len {:board (repeat 27 0) :three-d true})))

  (context "makes a new board that is"
    (it "2-D"
      (should= [0 0 0 0 0 0 0 0 0] (make-board {:size 3 :three-d false}))
      (should= [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0] (make-board {:size 4 :three-d false})))
    (it "3-D"
      (should= [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
               (make-board {:size 3 :three-d true}))
      (should= [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
               (make-board {:size 4 :three-d true}))))


  (context "places a value into the tttb"
    (it "checks if the position is empty to place"
      (should (spot-available? empty-board 0))
      (should-not (spot-available? [1 0 0 0 0 0 0 0 0] 0))
      (should-not (spot-available? [1 1 1 1 1 1 1 1 1] 4)))

    (it "an X into top left corner"
      (should= [0 0 2 0 0 0 0 0 0] (place-value-into-tttb empty-board 2 2)))
    (it "an X and O in opposite corners"
      (should= [1 0 0 0 0 0 0 0 2]
               (-> empty-board (place-value-into-tttb 1 0)
                   (place-value-into-tttb 2 8))))
    (it "an X on a spot where an O exists"
      (should= [2 0 0 0 0 0 0 0 0]
               (-> empty-board (place-value-into-tttb 2 0)
                   (place-value-into-tttb 1 0))))

    (it "a series of inputs"
      (should= mixed-board (-> empty-board
                               (place-value-into-tttb 1 0)
                               (place-value-into-tttb 2 2)
                               (place-value-into-tttb 2 4)
                               (place-value-into-tttb 1 5)
                               (place-value-into-tttb 2 6)
                               (place-value-into-tttb 1 7))))

    (it "returns board input if value is attempting to overwrite X or O"
      (should= mixed-board (place-value-into-tttb mixed-board 2 0))))

  (it "gives the absolute depth of the board"
      (should= 0 (get-absolute-depth [0 0 0 0 0 0 0 0 0]))
      (should= 1 (get-absolute-depth [0 0 0 0 0 0 0 0 1]))
      (should= 9 (get-absolute-depth [1 1 1 1 1 1 1 1 1]))))
