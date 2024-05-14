(ns ui-dispatch-interface_spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.symbols :as symbols]
            [tic-tac-toe.ui-dispatch-interface :refer :all]))

(describe "UI Dispatch Interface"
  (context "gets string representing tttb"

    (it "converts the board values to a more readable set of symbols"
      (should= [[(get symbols/symbols 1) (get symbols/symbols 2) " 2"]
                [(get symbols/symbols 2) " 4" (get symbols/symbols 1)]
                [" 6" (get symbols/symbols 1) (get symbols/symbols 2)]]
               (convert-board-vals-to-symbols [1 2 0 2 0 1 0 1 2]))
      (should= [[" 0" " 1" " 2" " 3"]
                [" 4" " 5" " 6" " 7"]
                [" 8" " 9" "10" "11"]
                ["12" "13" "14" "15"]]
               (convert-board-vals-to-symbols (repeat 16 0))))

    (it "an empty board"
      (should= "\n 0| 1| 2\n========\n 3| 4| 5\n========\n 6| 7| 8"
               (get-tttb-string {:board (repeat 9 0) :three-d false}))
      (should= "\n 0| 1| 2| 3\n===========\n 4| 5| 6| 7\n===========\n 8| 9|10|11\n===========\n12|13|14|15"
               (get-tttb-string {:board (repeat 16 0) :three-d false})))

    (it "a mixed board of x's and o's factoring in the color changes for x and o"
      (should= (str "\n" (get symbols/symbols 1) "| 1|"
                    (get symbols/symbols 2) "\n========\n 3|"
                    (get symbols/symbols 2) "|" (get symbols/symbols 1)
                    "\n========\n" (get symbols/symbols 2) "|"
                    (get symbols/symbols 1) "| 8")
               (get-tttb-string {:board [1 0 2 0 2 1 2 1 0] :three-d false})))

    (it "a 3-d board"
      (should= "\n 0| 1| 2\n========\n 3| 4| 5\n========\n 6| 7| 8\n\n 9|10|11\n========\n12|13|14\n========\n15|16|17\n\n18|19|20\n========\n21|22|23\n========\n24|25|26"
               (get-tttb-string {:board [(repeat 9 0) (repeat 9 0) (repeat 9 0)] :three-d true})))
    ))