library(ff)
library(data.table)
library(dplyr)
library(tidyr)

# (a)
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

# (b)
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

# (c)
flights.fn.lm <- function(dataset) {
  LM <- lm(DepDelay ~ ActualElapsedTime + CRSElapsedTime + ArrDelay,
           data = dataset)
  summary(LM)
  return(LM)
}

# (d)
rbenchmark::benchmark(
  replications = 1,
  flights.fn.lm(flights_2008_DT),
  flights.fn.lm(flights.2008.ff.data)
)

# (e)
flights.LM <- flights.fn.lm(flights_2008_DT)

flights_2007_DT <-
  fread(
    "2007.csv",
    sep = ",",
    header = TRUE,
    stringsAsFactors = FALSE,
    verbose = TRUE
  )

flights_2007_predict <- as.vector(predict(flights.LM, flights_2007_DT))
flights_2007_dep_delay <- as.vector(flights_2007_DT$DepDelay)
flights_2007_var <- var(flights_2007_predict - flights_2007_dep_delay, na.rm = TRUE)
# The varaince is very small, so the model is correct

# (f)
flights_DT <- rbind(flights_2007_DT, flights_2008_DT)
flights.ff.data <-
  read.table.ffdf(
    x = flights.2008.ff.data,
    file = "2007.csv",
    sep = ",",
    VERBOSE = TRUE,
    header = TRUE,
    next.rows = 1e5,
    colClasses = NA
  )
rbenchmark::benchmark(
  replications = 1,
  flights.fn.lm(flights_DT),
  flights.fn.lm(flights.ff.data)
)

