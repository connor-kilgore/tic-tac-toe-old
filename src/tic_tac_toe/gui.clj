(ns tic-tac-toe.gui
  (:require [clojure.string :as str]
            [quil.core :as q]
            [tic-tac-toe.symbols :as symbols]
            [tic-tac-toe.tic-tac-toe-board :as tttb]
            [tic-tac-toe.option-selector :as menu]
            [tic-tac-toe.ui-dispatch-interface :as ui]))

(def buttons (atom []))
(def state (atom nil))
(def selection (atom nil))

(defn clear-buttons []
  (reset! buttons []))

(defn clear-state []
  (reset! state nil))

(defn make-button [x y width height text val]
  (q/fill 200)
  (q/rect x y width height)
  (q/fill 0)
  (q/text-align :center)
  (q/text-font (q/create-font "Arial" 28 true))
  (q/text text (+ x (/ width 2)) (+ y (/ height 2)))
  (reset! buttons (conj @buttons {:x x :y y :width width :height height :text text :val val})))

(defn make-text [text]
  (q/fill 0)
  (q/text-align :center)
  (q/text-font (q/create-font "Arial" 28 true))
  (q/text text 400 50))

(defn button-clicked?
  ([button] (button-clicked? button (int (q/mouse-x)) (int (q/mouse-y))))
  ([button mouse-x mouse-y]
  (and (and (>= mouse-x (:x button)) (<= mouse-x (+ (:x button) (:width button))))
       (and (>= mouse-y (:y button)) (<= mouse-y (+ (:y button) (:height button)))))))

(defn get-button-clicked []
  (for [btn @buttons] (if (button-clicked? btn) btn nil)))

(defn mouse-pressed []
  (let [btn-clicked (first (filter #(not (nil? %)) (get-button-clicked)))]
    (if (not (nil? btn-clicked))
      (if (integer? (:val btn-clicked))
        (reset! selection (:val btn-clicked))
        (reset! selection (get @state (:val btn-clicked)))))))

(defn get-options-text [s]
  (let [split-s (str/split s #"\[")]
    (make-text (first split-s))
    (for [string (rest split-s)] (subs string (min 3 (count string))))))

(defn get-values [s]
  (let [split-s (str/split s #"\[")]
    (for [string (rest split-s)] (str (first string)))))

(defn show-options [options]
  (q/background 255)
  (let [options-text (get-options-text (:print-statement options))
        vals (get-values (:print-statement options))]
    (doseq [[index option] (map-indexed vector options-text)]
      (make-button 200 (+ 200 (* index 100)) 400 75 option (nth vals index)))))

(defmulti display-board :three-d)

(defmethod display-board true [game]
  (q/background 255)
  (let [row-len (int (Math/pow (count (:board game)) (/ 1 3)))
        side (/ 600 (* row-len row-len))]
    (doseq [i (range (count (:board game)))]
      (make-button (+ (+ 100 (* (mod i row-len) side)) (* row-len side (quot i (* row-len row-len))) )
                   (+ 100 (* (quot i row-len) side))
                   side side
                   (get symbols/gui-symbols (get (:board game) i))
                   i))))

(defmethod display-board false [game]
  (q/background 255)
  (let [row-len (int (Math/sqrt (count (:board game))))
        side (/ 600 row-len)]
    (doseq [i (range (count (:board game)))]
      (make-button (+ 100 (* (mod i row-len) side))
                   (+ 100 (* (quot i row-len) side))
                   side side
                   (get symbols/gui-symbols (get (:board game) i))
                   i))))

(defn wait-for-selection []
  (if (nil? @selection)
    (recur)))

(defmethod ui/get-selection :gui [options]
  (reset! state (:options options))
  (reset! selection nil)
  (wait-for-selection)
  @selection)

(defn update-state [new-state]
  (reset! state new-state)
  (Thread/sleep 350))

(defmethod ui/round-output :gui [game]
  (update-state game))

(defmethod ui/make-move :gui [game]
  (reset! state (:game game))
  (reset! selection nil)
  (wait-for-selection)
  (tttb/increment-round (assoc game
                          :board (tttb/place-value-into-tttb
                                   (:board (:game game)) (:symbol game) @selection))))

(defmethod ui/end-game :gui [end-condition-str]
  (update-state (:end-cond end-condition-str))
  (Thread/sleep 5000)
  (ui/get-selection {:options menu/retry-options :ui :gui}))

(defmethod ui/close-program :gui [_]
  (update-state :close-program))

(defn get-players [selection game]
  (cond (= selection :one-player) (ui/get-selection {:options menu/symbol-options :ui (:ui game)})
        (= selection :close-program) nil
        :else selection))

(defn get-difficulty [players game]
  (if (or (nil? players) (not (:has-ai players)))
    nil
    (ui/get-selection {:options menu/difficulty-options :ui (:ui game)})))

(defn get-dimensions [selection game]
  (if (= selection :close-program)
    nil
    (ui/get-selection {:options menu/board-size-options :ui (:ui game)})))

(defmethod ui/initialize-game :gui [game]
  (let [selection (ui/get-selection {:options menu/menu-options :ui (:ui game)})
        players (get-players selection game)
        difficulty (get-difficulty players game)
        dimensions (get-dimensions selection game)]
    (if (= selection :close-program)
      selection
      (-> game
          (assoc :players (:players players))
          (assoc :difficulty difficulty)
          (assoc :three-d (:three-d dimensions))
          (assoc :board (tttb/make-board dimensions))))))

(defn setup []
  (q/frame-rate 10)
  (q/background 255))

(defn draw []
  (clear-buttons)
  (cond (not (nil? (:print-statement @state))) (show-options @state)
        (string? @state) (make-text @state)
        (= @state :close-program) (q/exit)
        (not (nil? (:board @state))) (display-board @state)))

(defmethod ui/initialize-ui :gui [_]
  (q/defsketch sketch
    :title "Tic Tac Toe"
    :size [800 800]
    :setup setup
    :draw draw
    :update show-options
    :mouse-pressed mouse-pressed))