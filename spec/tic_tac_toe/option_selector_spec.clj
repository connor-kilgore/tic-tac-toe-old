(ns tic-tac-toe.option-selector-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.game-runner :as run]
            [tic-tac-toe.option-selector :refer :all]
            [tic-tac-toe.tui :as tui]))

(describe "Option Selector"
  (context "gives a function based on a start menu option"
    (it "nil if invalid selection"
      (should= nil (get-option menu-options "0")))

    (it "close program if 4"
      (should= :close-program (get-option menu-options "4"))))
  )
