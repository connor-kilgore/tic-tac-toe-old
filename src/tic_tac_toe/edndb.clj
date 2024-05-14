(ns tic-tac-toe.edndb
  (:require [tic-tac-toe.save-interface :as save]))

(def saved-games-dir "/Users/connor_kilgore/Desktop/CleanCoders/apprenticeship/projects/tic-tac-toe/saved-games/")
(def last-game-path (str saved-games-dir "/last.txt"))

(defn append-game-state-to-file [path game]
  (spit path (str game "\n") :append true))

(defmethod save/save-game! :edndb [game]
  (append-game-state-to-file last-game-path game))

(defmethod save/get-last-game :edndb [_]
  (try
    (read-string (str "[" (slurp last-game-path) "]"))
    (catch Exception _ nil)))

(defmethod save/get-game-archive :edndb [game]
  (let [path (str saved-games-dir (first (:parameters game)))]
    (try
      (read-string (str "[" (slurp path) "]"))
      (catch Exception _ nil))))

(defn wipe-last-game []
  (spit last-game-path ""))

(defmethod save/archive-game :edndb [_]
  (spit (str saved-games-dir (save/get-save-name) ".txt") (slurp last-game-path))
  (wipe-last-game))