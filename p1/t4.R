library(rsparkling)
library(sparklyr)
library(dplyr)
library(tidyr)
library(h2o)
library(data.table)

h2o.init()
options(rsparkling.sparklingwater.version = "2.3.10")
options(rsparkling.sparklingwater.location = "sparkling-water-assembly_2.11-2.3.10-all.jar")


spark_install(version = "2.3.0")

sc <- spark_connect(master = "local", version = "2.3.0")

song_tbl <- spark_read_csv(sc, "msd", "msd_onevalue.csv", sep = ",", header = TRUE)

#song = fread("msd_onevalue.csv", sep = ",", header = TRUE)

#part(b)--------------
#selection = c("artist_familiarity","artist_hotttnesss","duration","loudness","tempo","song_id")
feature = c("artist_familiarity","artist_hotttnesss","duration","loudness","tempo")
#song_clean = song[,names(song) %in% selection,with=FALSE]

song_clean_tbl <- song_tbl %>%
  select(artist_familiarity, artist_hotttnesss, duration, loudness, tempo, song_id)

(song.kmeans1 = h2o.kmeans(training_frame = song_h2o, k = 1, x = feature))

spark_write_csv(song_clean_tbl, "clean.csv")

glm <- h2o.glm(x = c("artist_familiarity", "artist_hotttnesss"), 
                      y = "duration",
                      training_frame = song_h2o,
                      lambda_search = TRUE)

(song.kmeans2 = h2o.kmeans(training_frame = song_h2o, k = 2, x = feature))
(song.kmeans3 = h2o.kmeans(training_frame = song_h2o, k = 3, x = feature))
(song.kmeans4 = h2o.kmeans(training_frame = song_h2o, k = 4, x = feature))
(song.kmeans5 = h2o.kmeans(training_frame = song_h2o, k = 5, x = feature))

spark_disconnect(sc)






