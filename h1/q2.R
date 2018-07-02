hints.func = function(n) {
  n1 <- 3 * n
  f <- n %% 2 == 0
  if (f) {
    n2 <- n1 / 2
  } else {
    n2 <- (n1 + 1) / 2
  }
  n3 <- 3 * n2
  k <- floor(n3 / 9)
  return(list(k=k, f=f))
}

guess.func = function(k, f) {
  if (f) {
    n = 2 * k
  } else {
    n = 2 * k + 1
  }
  return(n)
}

hints.func(10)
guess.func(5, TRUE)


