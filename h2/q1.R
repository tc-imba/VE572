library(tidyr)
library(dplyr)
library(data.table)
library(rbenchmark)

method1_fn <- function() {
  who_tidy_tb <- tidyr::who %>%
    gather(
      new_sp_m014:newrel_f65,
      key = "tmp",
      value = "counts",
      na.rm = TRUE
    ) %>%
    mutate(tmp = stringr::str_replace(tmp, "newrel", "new_rel")) %>%
    separate(
      col = tmp,
      sep = "_",
      into = c("new", "type", "sexage")
    ) %>%
    select(-new,-iso2 ,-iso3) %>%
    separate(col = sexage ,
             into = c("gender", "age"),
             sep = 1)
}

method2_fn <- function() {
  who_dt <- data.table(tidyr::who)
  
  who_long_dt <-
    melt(
      who_dt,
      id.vars = c("country", "iso2", "iso3", "year"),
      variable.name = "tmp",
      value.name = "counts",
      na.rm = TRUE
    )
  
  who_long_dt <-
    mutate(who_long_dt,
           tmp = stringr::str_replace(tmp, "newrel", "new_rel")) %>%
    separate(
      col = tmp,
      sep = "_",
      into = c("new", "type", "sexage")
    ) %>%
    separate(col = sexage,
             sep = 1,
             into = c("gender", "age"))
  
  nwho_dt <-
    select(who_long_dt, -new, -iso2, -iso3)
  
}

benchmark(
  replications = 10,
  method1_fn(),
  method2_fn(),
  columns = c('test', 'elapsed', 'replications')
)
