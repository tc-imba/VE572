threecars.df <-
  read.table('three_cars.csv', header = TRUE, sep = ",")
a <- threecars.df$Price[threecars.df$BMW == 1]
b <- threecars.df$Price[threecars.df$Jaguar == 1]
c <- threecars.df$Price[threecars.df$Porsche == 1]
pdf("q5b.pdf")
boxplot(
  a,
  b,
  c,
  names = c("BMW", "Jaguar", "Porsche"),
  main = "Car price by brand",
  ylab = "Price"
)
dev.off()
