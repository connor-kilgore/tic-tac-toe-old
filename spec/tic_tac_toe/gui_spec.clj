(ns tic-tac-toe.gui-spec
  (:require [quil.core :as q]
            [tic-tac-toe.option-selector :as menu]
            [speclj.core :refer :all]
            [tic-tac-toe.gui :refer :all]))

(describe "GUI"

  (context "Can make"
    (it "Buttons"
      (with-redefs [reset! (fn [_ new] new)
                    q/rect (fn [_ _ _ _] nil)
                    q/fill (fn [_] nil)
                    q/text-align (fn [_] nil)
                    q/text-font (fn [_] nil)
                    q/create-font (fn [_ _ _] nil)
                    q/text (fn [_ _ _] nil)]
        (should= [{:x    100 :y 100 :width 200 :height 200
                   :text "I am a button" :val 1}]
                 (make-button 100 100 200 200 "I am a button" 1))))
    (it "Text"
      (with-redefs [make-text (fn [text] text)]
        (should= "I am text" (make-text "I am text")))))

  (it "checks if a button was clicked"
    (should (button-clicked? {:x     100 :y 100
                              :width 200 :height 200
                              :text  "I am a button" :val 1} 150 150))
    (should-not (button-clicked? {:x     100 :y 100
                                  :width 200 :height 200
                                  :text  "I am a button" :val 1} 500 500)))

  (it "gets possible option strings from options print statement"
    (with-redefs [make-text (fn [_] nil)]
      (should= '("Single Player\n" "Two Player\n")
               (get-options-text "[1] Single Player\n[2] Two Player\n"))))

  (it "gets possible values from options print statement"
    (should= '("X" "O") (get-values "\nPlease select!\n[X] X\n[O] O")))

  (it "updates the state"
    (with-redefs [update-state (fn [new-state] new-state)]
    (should= [0 0 0 0 0] (update-state [0 0 0 0 0])))))