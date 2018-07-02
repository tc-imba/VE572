temp <-
  url('https://www.stat.ncsu.edu/research/sas/sicl/data/whisky.dat',
      open = "r")
A <-
  scan(text = paste(readLines(temp, 2), collapse = ""),
       sep = ",")
B <-
  scan(text = paste(readLines(temp), collapse = ""),
       sep = ",")
t.test(A, B)
