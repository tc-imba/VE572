# Read integers_letters.dbf into R as a data frame, and name it il.df.
require(foreign)
i1.df <- read.dbf('integers_letters.dbf', as.is = TRUE)

# Write R statement/s to find the integer/s between 1 and 26 that is missing.
miss = NULL
for (i in 1:26) {
  if (sum(i1.df$INTEGERS == i) == 0) {
    miss = c(miss, i)
  }
}
miss

# Write R statement/s to find the letter/s that is/are capitalised.
i1.df$LETTERS[i1.df$LETTERS == toupper(i1.df$LETTERS)]
