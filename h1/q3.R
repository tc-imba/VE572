set.seed(572)
u = runif(1e6)
x = as.numeric(u <= 0.3)

gap.freq <- function(x, m = 3) {
  begin <- TRUE
  count <- 0
  len <- sum(x) - 1
  v <- vector(length = len)
  index = 1
  for (num in x) {
    if (num) {
      if (!begin) {
        if (count < m) {
          v[index] <- count
        } else {
          v[index] <- m
        }
        index <- index + 1
      }
      begin <- FALSE
      count <- 0
    } else {
      count <- count + 1
    }
  }
  t <- table(v)
  names(t)[m + 1] = paste(m, "+", sep = "")
  return(t)
}

gap.freq(x, 10)
