(ns tic-tac-toe.edndb_spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.edndb :refer :all]
            [tic-tac-toe.save-interface :as save]))

(def game-state {:players {"Player" 1, "AI" 2},
                 :difficulty 10, :round 1,
                 :three-d false, :board [0 0 0 0 0 0 0 0 0],
                 :ui :tui, :save-location :edndb,
                 :parameters ()})

(describe "File Saver"

  (context "last game can be deleted by"
    (it "wiping the old content"
      (should= nil (wipe-last-game)))
    (it "retrieving empty list if file is empty"
      (should= [] (save/get-last-game game-state)))
    (it "retrieving the game if not"
      (with-redefs [last-game-path "saved-games/test.txt"]
        (should= (read-string (str "[" (slurp last-game-path) "]")) (save/get-last-game game-state)))))

  (context "last game is retrievable by"
    (it "saving the game state to a json file each round"
      (should= nil (save/save-game! game-state)))

    (it "getting the last game state from a file"
      (should= game-state (first (save/get-game-archive (assoc game-state :parameters ["test.txt"]))))))

  (it "gets the archive a specified game"
    (should= [{:players {"Player" 1, "AI" 2}, :difficulty 10, :round 1, :three-d false, :board [0 0 0 0 0 0 0 0 0], :ui :tui, :save-location :edndb, :parameters ()}
             {:players {"Player" 1, "AI" 2}, :difficulty 10, :round 2, :three-d false, :board [0 0 1 0 0 0 0 0 0], :ui :tui, :save-location :edndb, :parameters ()}
             {:players {"Player" 1, "AI" 2}, :difficulty 10, :round 3, :three-d false, :board [0 0 1 0 2 0 0 0 0], :ui :tui, :save-location :edndb, :parameters ()}
             {:players {"Player" 1, "AI" 2}, :difficulty 10, :round 4, :three-d false, :board [0 1 1 0 2 0 0 0 0], :ui :tui, :save-location :edndb, :parameters ()}
             {:players {"Player" 1, "AI" 2}, :difficulty 10, :round 5, :three-d false, :board [2 1 1 0 2 0 0 0 0], :ui :tui, :save-location :edndb, :parameters ()}
             {:players {"Player" 1, "AI" 2}, :difficulty 10, :round 5, :three-d false, :board [2 1 1 0 2 0 0 0 0], :ui :tui, :save-location :edndb, :parameters ()}
             {:players {"Player" 1, "AI" 2}, :difficulty 10, :round 6, :three-d false, :board [2 1 1 0 2 1 0 0 0], :ui :tui, :save-location :edndb, :parameters ()}
             {:players {"Player" 1, "AI" 2}, :difficulty 10, :round 7, :three-d false, :board [2 1 1 0 2 1 0 0 2], :ui :tui, :save-location :edndb, :parameters ()}]
             (save/get-game-archive (assoc game-state :parameters ["test.txt"])))))
