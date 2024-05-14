(ns tic-tac-toe.save-interface
  (:import (java.text SimpleDateFormat)
           (java.util Date)))

(defmulti save-game! :save-location)

(defmulti get-last-game :save-location)

(defmulti archive-game :save-location)

(defmulti get-game-archive :save-location)

(defn get-save-name []
  (str (.format (SimpleDateFormat. "yyyyMMdd_HHmmss") (Date.))))