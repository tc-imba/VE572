library(ff)
library(data.table)

options(fftempdir = "./ffdf")
flights.2008.ff.data <-
  read.table.ffdf(
    file = "2008.csv",
    sep = ",",
    VERBOSE = TRUE,
    header = TRUE,
    next.rows = 1e5,
    colClasses = NA
  )

flights_2008_DT <-
  fread(
    "2008.csv",
    sep = ",",
    header = TRUE,
    stringsAsFactors = FALSE,
    verbose = TRUE
  )

flights.LM = lm(DepDelay ~ DayOfWeek + DepTime + CRSDepTime + ArrTime +
                  CRSArrTime + UniqueCarrier ,
                data = flights_2008_DT)

pca <-
  prcomp(
    ~ DayOfWeek + DepTime + CRSDepTime + ArrTime + CRSArrTime,
    data = flights_2008_DT,
    scale = TRUE
  )

summary(pca)
pca
