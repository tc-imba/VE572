# the area of the largest landmass.
max(islands)
# the number of landmasses with areas between 100 and 1000 square miles.
sum(islands >= 100 & islands <= 1000)
# the ranking of the area of the North Island of New Zealand (New Zealand (N)) in the world.
sorted <- sort(islands, decreasing = TRUE)
r1 <- which(names(sorted) == "New Zealand (N)")
# the name of the landmass that has the most similar area to New Zealand (North and South Islands).
r2 <- which(names(sorted) == "New Zealand (S)")
names(which.min(abs(sorted[sorted != sorted[r1] &
                             sorted != sorted[r2]] - sorted[r1] - sorted[r2])))
# the names of the top 10 largest landmasses.
names(sorted[1:10])