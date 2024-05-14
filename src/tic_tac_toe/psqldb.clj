(ns tic-tac-toe.psqldb
  (:require [clojure.java.jdbc :as jdbc]
            [tic-tac-toe.save-interface :as save]))

(def db
  {:dbtype "postgresql"
   :dbname "tictactoe"
   :host   "localhost"
   :user   "connor_kilgore"})

(def last-game-path "last")

(defn get-game-from-games-table [game-key]
  (first (jdbc/query db ["SELECT * FROM games WHERE file = ?" game-key])))

(defn get-board-entries-map [game game-key]
  (for [[i v] (map-indexed vector (:board game))]
    {:file game-key :index i :round (:round game) :value v}))

(defn save-board-to-db [game game-key]
  (let [board-entries (get-board-entries-map game game-key)]
    (try
      (jdbc/insert-multi! db :boards board-entries)
      (catch Exception e
        nil))))

(defn update-board-to-db [game game-key]
  (let [board-entries (get-board-entries-map game game-key)]
    (doseq [entry board-entries]
      (jdbc/update! db :boards entry ["file = ? and index = ? and round = ?"
                                      game-key (:index entry) (:round entry)]))))

(defn get-game-row [game game-key]
  {:file       game-key :players (str (:players game))
   :difficulty (:difficulty game)
   :three_d    (:three-d game)})

(defn save-game-to-db [game game-key]
  (let [game-row (get-game-row game game-key)]
    (try
      (jdbc/insert! db :games game-row)
      (catch Exception e
        nil))))

(defn get-current-round-of-game [game-key]
  (-> (jdbc/query db ["SELECT MAX(round) from boards where file = ?" game-key])
      (first) (:max)))

(defn get-size-of-game [game-key]
  (-> (jdbc/query db ["SELECT MAX(index) from boards where file = ?" game-key])
      (first) (:max) (inc)))

(defn get-board-of-game-at-round [game-key round]
  (let [board-details (jdbc/query db [(str "select * from games inner join boards on "
                                           "games.file = boards.file where games.file = ?"
                                           "and round = ?;")
                                      game-key round])]
    (for [board-info board-details]
      (:value board-info))))

(defn get-boards-of-game [game-key]
  (for [round (range 1 (inc (get-current-round-of-game game-key)))]
    (get-board-of-game-at-round game-key round)))

(defn delete-game [game-key]
  (concat
    (jdbc/delete! db :boards ["file = ?" game-key])
    (jdbc/delete! db :games ["file = ?" game-key])))

(defmethod save/save-game! :psqldb [game]
  (save-game-to-db game last-game-path)

  (let [saved-game (save-board-to-db game last-game-path)]
    (if (nil? saved-game)
      (update-board-to-db game last-game-path)
      saved-game)))

(defmethod save/get-last-game :psqldb [game]
  (try
    (let [game-details (first (jdbc/query db ["SELECT * FROM games WHERE file = ?" last-game-path]))
          boards (if (nil? game-details) nil (get-boards-of-game last-game-path))]
      (for [[i board] (map-indexed vector boards)]
        {:players       (read-string (:players game-details))
         :difficulty    (:difficulty game-details)
         :round         (inc i)
         :three-d       (:three_d game-details)
         :board         (into [] board)
         :ui            (:ui game)
         :save-location (:save-location game)
         :parameters    (:parameters game)}))
    (catch Exception e nil)))

(defmethod save/archive-game :psqldb [game]
  (let [last-game (save/get-last-game game)]
    (doseq [game-state last-game]
      (with-redefs [last-game-path (save/get-save-name)]
        (save/save-game! game-state))))
  (delete-game last-game-path))

(defmethod save/get-game-archive :psqldb [game]
  (let [game-key (first (:parameters game))]
    (with-redefs [last-game-path game-key]
      (save/get-last-game game))))