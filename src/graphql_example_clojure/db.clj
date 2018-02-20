(ns graphql-example-clojure.db
  (:require [datomic.api :as d]))

(def uri "datomic:dev://localhost:4334/mbrainz-1968-1973")

(defn conn [] (d/connect uri))

(defn db [] (d/db (conn)))

(defn artist-by-name
  [name]
  (d/q '[:find  (pull ?a [*]) .
         :in $ ?artist-name
         :where
         [?a :artist/name ?artist-name]]
       (db)
       name))

(defn tracks-by-name
  [name]
  (d/q '[:find  [(pull ?t [*]) ...]
         :in $ ?track-name
         :where
         [?t :track/name ?track-name]]
       (db)
       name))

(defn release-by-name
  [name]
  (d/q '[:find  (pull ?r [*]) .
         :in $ ?release-name
         :where
         [?r :release/name ?release-name]]
       (db)
       name))

(defn releases-by-artist-name
  [artist-name]
  (d/q '[:find [(pull ?r [*]) ...]
         :in $ ?artist-name
         :where
         [?a :artist/name ?artist-name]
         [?r :release/artists ?a]]
       (db)
       artist-name))

(defn entity-by-id
  [id]
  (d/pull (db) '[*] (:db/id id)))

(defn tracks-by-artist
  [artist-id]
  (d/q '[:find (pull ?t [*])
         :in $ ?artist-id
         :where
         [?t :track/artists ?artist-id]]
       (db)
       artist-id))

(defn artists-by-release
  [release-id]
  (:release/artists
   (d/pull (db) '[{:release/artists [*]}] release-id)))

(comment



  (releases-by-artist-name "The Rolling Stones")

  (artists-by-release
          (:db/id (first (releases-by-artist-name "The Rolling Stones"))))


  (tracks-by-name "Prodigal Son")

  (filter #(not= "Official" (:release/status %)) (releases-by-artist-name "The Rolling Stones"))

  (count
   (tracks-by-artist
    (:db/id
     (d/q '[:find (pull ?a [*]) .
            :in $ ?artist-name
            :where
            [?a :artist/name ?artist-name]]
          db
          "The Rolling Stones"))))


  )