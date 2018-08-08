library(rsparkling)
library(sparklyr)
library(dplyr)
library(tidyr)
library(h2o)
library(data.table)

# An example dataset file of about 4 MB (1%)
filename = "msd_example.csv"

# The all dataset file of about 400 MB
# filename = "msd_onevalue.csv"

h2o.init()
options(rsparkling.sparklingwater.version = "2.3.10")
options(rsparkling.sparklingwater.location = "sparkling-water-assembly_2.11-2.3.10-all.jar")

spark_install(version = "2.3.0")
sc <- spark_connect(master = "local", version = "2.3.0")

song_tbl <- spark_read_csv(sc, "msd", filename, sep = ",", header = TRUE)

song_clean_tbl <- song_tbl %>%
  mutate(artist_familiarity_ = as.numeric(artist_familiarity)) %>%
  mutate(artist_hotttnesss_ = as.numeric(artist_hotttnesss)) %>%
  mutate(duration_ = as.numeric(duration)) %>%
  mutate(loudness_ = as.numeric(loudness)) %>%
  mutate(tempo_ = as.numeric(tempo)) %>%
  select(artist_familiarity_, artist_hotttnesss_, duration_, loudness_, tempo_, song_id)

feature = c("artist_familiarity_","artist_hotttnesss_","duration_","loudness_","tempo_")

song_h2o <- as_h2o_frame(sc, song_clean_tbl, strict_version_check = FALSE)


(song.kmeans1 = h2o.kmeans(training_frame = song_h2o, k = 1, x = feature))
(song.kmeans2 = h2o.kmeans(training_frame = song_h2o, k = 2, x = feature))
(song.kmeans3 = h2o.kmeans(training_frame = song_h2o, k = 3, x = feature))
(song.kmeans4 = h2o.kmeans(training_frame = song_h2o, k = 4, x = feature))
(song.kmeans5 = h2o.kmeans(training_frame = song_h2o, k = 5, x = feature))

spark_disconnect(sc)






