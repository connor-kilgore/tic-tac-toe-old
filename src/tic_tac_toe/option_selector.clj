(ns tic-tac-toe.option-selector)

(defmulti close-program :ui)

(def retry-options
  {:print-statement "\nWould you like to try again?\n[1] Yes\n[2] No"
   :error           "\nPlease select a valid option."
   "1"              true
   "2"              false})

(def symbol-options
  {:print-statement "\nPlease select a symbol!\n[X] X (first turn).\n[O] O (second turn)."
   :error           "\nPlease select a valid symbol."
   "x"              {:has-ai true :players {"Player" 1 "AI" 2}}
   "X"              {:has-ai true :players {"Player" 1 "AI" 2}}
   "o"              {:has-ai true :players {"Player" 2 "AI" 1}}
   "O"              {:has-ai true :players {"Player" 2 "AI" 1}}})

(def move-options
  {9  (conj
        {:print-statement "\nSelect a space [0-8]"
         :error           "\nPlease select a valid space."}
        (zipmap (map str (range 9)) (range 9)))
   16 (conj
        {:print-statement "\nSelect a space [0-15]"
         :error           "\nPlease select a valid space."}
        (zipmap (map str (range 16)) (range 16)))
   27 (conj
        {:print-statement "\nSelect a space [0-26]"
         :error           "\nPlease select a valid space."}
        (zipmap (map str (range 27)) (range 27)))})

(def difficulty-options
  {:print-statement "\nPlease select a difficulty!\n[1] Unbeatable\n[2] Medium\n[3] Easy"
   :error           "\nPlease select a valid difficulty."
   "1"              10
   "2"              9
   "3"              -1})

(def menu-options
  {:print-statement "\nPlease select an option!\n[1] Single Player\n[2] Two Player\n[3] AI vs. AI\n[4] Close Program"
   :error           "\nPlease select a valid option."
   "1"              :one-player                             ;(fn [] (initialize-one-player))
   "2"              {:has-ai false :players {"Player 1" 1 "Player 2" 2}}
   "3"              {:has-ai true :players {"AI 1" 1 "AI 2" 2}}
   "4"              :close-program})

(def board-size-options
  {:print-statement "\nPlease select a board side length!\n[1] 3x3 (classic)\n[2] 4x4\n[3] 3x3x3"
   :error           "\nPlease select a valid size!"
   "1"              {:size 3 :three-d false}
   "2"              {:size 4 :three-d false}
   "3"              {:size 3 :three-d true}})

(defn get-option [selected-map option]
  (get selected-map option))


