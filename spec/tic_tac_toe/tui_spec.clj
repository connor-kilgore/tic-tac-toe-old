(ns tic-tac-toe.tui-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.option-selector :as menu]
            [tic-tac-toe.tui :refer :all]
            [tic-tac-toe.ui-dispatch-interface :as ui]))

(def new-game
  {:players       {"Player" 1, "AI" 2},
   :difficulty    10, :round 1, :three-d false,
   :board         [0 0 0 0 0 0 0 0 0], :ui :tui,
   :save-location :edndb, :parameters ()})

(describe "TUI"
  (it "prints text given to the console"
    (should= "Hello World!\n" (with-out-str (ui/print-text {:ui :tui :text "Hello World!"}))))

  (context "gets the player of symbol"
    (it "X on odd rounds"
      (should= ["Player" 1] (get-current-player new-game)))
    (it "O on odd rounds"
      (should= ["AI" 2] (get-current-player (assoc new-game :round 2)))))

  (it "output of the current round is printed"
    (should= "\n===  Player  === ROUND  1  ===\n\n 0| 1| 2\n========\n 3| 4| 5\n========\n 6| 7| 8\n"
             (with-out-str (ui/round-output new-game))))

  (it "has user repeat input until a valid selection of options is made"
    (with-redefs [println (fn [_] nil)
                  read-line (fn [] "1")]
      (should= :one-player (ui/get-selection
                             {:ui :tui :options menu/menu-options}))))

  (context "parses players on"
    (it "close-program"
      (should= nil (get-players :close-program new-game)))
    (it "one-player"
      (with-redefs [ui/get-selection (fn [_] {"Player" 1 "AI" 2})]
        (should= {"Player" 1 "AI" 2} (get-players :one-player new-game))))
    (it "other"
      (should= {"Player 1" 1 "Player 2" 2}
               (get-players {"Player 1" 1 "Player 2" 2} new-game))))

  (context "parses difficulty with"
    (it "no players"
      (should= nil (get-difficulty nil new-game)))
    (it "no ai players"
      (should= nil (get-difficulty {:has-ai false :players {"Player 1" 1 "Player 2" 2}} new-game)))
    (it "ai players"
      (with-redefs [ui/get-selection (fn [_] 10)]
        (should= 10 (get-difficulty {:has-ai  true
                                     :players {"Player" 1 "AI" 2}} new-game)))))

  (context "parses dimensions with"
    (it "close program"
      (should= nil (get-dimensions :close-program new-game)))
    (it "a 2d board"
      (with-redefs [ui/get-selection (fn [_] {:size 3 :three-d false})]
        (should= {:size 3 :three-d false} (get-dimensions 1 new-game))))
    (it "a 3d board"
      (with-redefs [ui/get-selection (fn [_] {:size 3 :three-d true})]
        (should= {:size 3 :three-d true} (get-dimensions 1 new-game)))))

  (context "initializes a game by"
    (it "closing the program when called"
      (with-redefs [ui/get-selection (fn [_] :close-program)]
        (should= :close-program (ui/initialize-game new-game))))
    (it "setting all the values based on user selection"
      (with-redefs [ui/get-selection (fn [_] {:has-ai true :players {"Player" 1 "AI" 2}})
                    get-difficulty (fn [_ _] 10)
                    get-dimensions (fn [_ _] {:size 3 :three-d false})]
        (should= new-game (ui/initialize-game new-game)))))

  (it "prompts user to make a move on the board"
    (with-redefs [println (fn [_] nil)
                  read-line (fn [] "2")]
      (should= [0 0 1 0 0 0 0 0 0] (:board (ui/make-move
                                             {:game new-game :ui :tui :symbol 1})))))

  (it "gives an end game message"
    (with-redefs [ui/get-selection (fn [_] nil)]
      (should= "Player wins!\n"
               (with-out-str (ui/end-game {:ui :tui :end-cond "Player wins!"})))))

  (it "prints close program message"
    (should= "\nClosing program. Goodbye.\n"
             (with-out-str (ui/close-program new-game))))

  (it "prints initialize ui message"
    (should= "\nWelcome to the Unbeatable Tic-Tac-Toe Game!\n"
             (with-out-str (ui/initialize-ui new-game)))))