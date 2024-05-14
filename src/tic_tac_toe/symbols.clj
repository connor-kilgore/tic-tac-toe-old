(ns tic-tac-toe.symbols)

(def symbols
  {0 0
   1 (str "\u001b[38;5;" 1 "m" " X" "\u001b[0m")
   2 (str "\u001b[38;5;" 4 "m" " O" "\u001b[0m")})

(def gui-symbols
  {0 " "
   1 "X"
   2 "O"})

(def reverse-symbols
  {1 2
   2 1})

(defn reverse-symbol [symbol]
  (get reverse-symbols symbol))

(defn symbol-at [position board]
  (get symbols (get board position)))