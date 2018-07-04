# Task 1 part (a)
library(ISLR)
cor(Smarket)
cor(Smarket[,-9])

library(corrplot)
corrplot(cor(Smarket[,-9]),
         method = "ellipse",
         type = "upper",
         diag = FALSE)


# Task 1 part (b)
s.GLM = glm(Direction ~ Lag1 + Lag2 + Lag3 + Lag4 + Lag5 + Volume,
            data = Smarket,
            family = binomial)
summary(s.GLM)

# Task 1 part (c)
pred.df = data.frame(
  Year = 2001,
  Lag1 = 0.5,
  Lag2 = 0.5,
  Lag3 = 0.5 ,
  Lag4 = 0.5 ,
  Lag5 = 0.5 ,
  Volume = 1 ,
  Today = 0.6
)
predict (s.GLM, pred.df, type = "response")
head ({
  probs.training = predict(s.GLM, type = "response")
})
probs.training.binarization = probs.training >= 0.5
probs.real.binarization = Smarket$Direction == "Up"
probs.equal = sum(probs.training.binarization == probs.real.binarization)
probs.rate = probs.equal / length(probs.training)

# Task 1 part (d)
training = (Smarket$Year < 2005)
Smarket.test = Smarket[!training,]
dim(Smarket)
dim(Smarket.test)

s.GLM.new = glm(
  Direction ~ Lag1 + Lag2 + Lag3 + Lag4 + Lag5 + Volume,
  data = Smarket,
  family = binomial,
  subset = training
)
summary(s.GLM.new)
probs.training.new = predict(s.GLM.new, Smarket.test, type = "response")
probs.equal.new = sum((probs.training.new >= 0.5) == (Smarket.test$Direction == "Up"))
probs.rate.new = probs.equal.new / length(probs.training.new)

# Task 1 part (e)
library(class)
train.X = cbind(Smarket$Lag1, Smarket$Lag2)[training,]
test.X = cbind(Smarket$Lag1, Smarket$Lag2)[!training,]
train.Direction = Smarket$Direction [training]
set.seed(1)
knn.pred = knn (
  train = train.X,
  test = test.X,
  cl = train.Direction,
  k = 3
)
table(knn.pred, Smarket$Direction [!training])

# Task 2 part (a)
corrplot(cor(Weekly[,-9]),
         method = "ellipse",
         type = "upper",
         diag = FALSE)

# Task 2 part (b)
s.GLM = glm(Direction ~ Lag1 + Lag2 + Lag3 + Lag4 + Lag5 + Volume,
            data = Weekly,
            family = binomial)
summary(s.GLM)

# Task 2 part (c)
training = (Weekly$Year <= 2008)
s.GLM = glm(Direction ~ Lag2,
            data = Weekly,
            family = binomial,
            subset = training)
summary(s.GLM)
Weekly.test = Weekly[!training,]
probs.training = predict(s.GLM, Weekly.test, type = "response")
probs.equal = sum((probs.training >= 0.5) == (Weekly.test$Direction == "Up"))
probs.rate = probs.equal / length(probs.training)

# Task 2 part (d)
train.X = data.frame(Weekly$Lag2[training])
test.X = data.frame(Weekly$Lag2[!training])
train.Direction = Weekly$Direction[training]
set.seed(1)
knn.pred = knn (
  train = train.X,
  test = test.X,
  cl = train.Direction,
  k = 1
)
table(knn.pred, Weekly$Direction[!training])


# Task 2 part (e)
training = (Weekly$Year <= 2008)
s.GLM = glm(
  Direction ~ Lag1 + Lag2,
  data = Weekly,
  family = binomial,
  subset = training
)
summary(s.GLM)
Weekly.test = Weekly[!training,]
probs.training = predict(s.GLM, Weekly.test, type = "response")
probs.equal = sum((probs.training >= 0.5) == (Weekly.test$Direction == "Up"))
probs.rate = probs.equal / length(probs.training)

set.seed(1)
knn.pred = knn (
  train = train.X,
  test = test.X,
  cl = train.Direction,
  k = 3
)
table(knn.pred, Weekly$Direction[!training])

train.X = cbind(Weekly$Lag1, Weekly$Lag2)[training,]
test.X = cbind(Weekly$Lag1, Weekly$Lag2)[!training,]
train.Direction = Weekly$Direction [training]
set.seed(1)
knn.pred = knn (
  train = train.X,
  test = test.X,
  cl = train.Direction,
  k = 3
)
table(knn.pred, Smarket$Direction [!training])

# Task 3 part (a)
set.seed (2)
x = matrix (rnorm (50 * 2) , ncol = 2)
x [1:25 , 1] = x [1:25 , 1] + 3
x [1:25 , 2] = x [1:25 , 2] - 4

# Task 3 part (b)
cl2 = kmeans(x, centers = 2, nstart = 20)

# Task 3 part (c)
plot(x, col = cl2$cluster)
points(cl2$centers,
       col = 1:2,
       pch = 8,
       cex = 2)

# Task 3 part (d)
cl3 = kmeans(x, centers = 3, nstart = 20)
plot(x, col = cl3$cluster)
points(cl3$centers,
       col = 1:4,
       pch = 8,
       cex = 2)

# Task 3 part (e)
hc = hclust(dist(x, method = "euclidean"))

# Task 3 part (f)
plot(hc)

# Task 3 part (g)
# Cuts a tree, e.g., as resulting from hclust, into several groups either by specifying the desired number(s) of groups or the cut height(s).
ct1 = cutree(hc, k = 1:5)
ct2 = cutree(hc, h = 2)

# Task 3 part (h)
xs = scale(x)
hcs = hclust(dist(xs, method = "euclidean"))
plot(hcs)

# Task 3 part (i)
corrplot(cor(t(x)) ,
         method = "ellipse",
         type = "upper",
         diag = FALSE)

# Task 4 part (a)
library(arules)
library(arulesViz)
data(Groceries)
itemFrequencyPlot(Groceries, topN = 20, type = "absolute")

# Task 4 part (b)
rules = apriori(Groceries, parameter = list(
  supp = 0.001,
  conf = 0.8,
  target = "rules"
))

# Task 4 part (c)
inspect(rules[1:5])
summary(rules)

# Task 4 part (d)
rules.sorted = sort(rules, by = "confidence")

# Task 4 part (e)
## (i)
rules.i = apriori(
  Groceries,
  parameter = list(
    supp = 0.001,
    conf = 0.8,
    target = "rules"
  ),
  appearance = list(rhs = "whole milk")
)
summary(rules.i)

## (ii)
rules.ii = apriori(
  Groceries,
  parameter = list(
    supp = 0.001,
    conf = 0.2,
    target = "rules"
  ),
  appearance = list(lhs = "whole milk")
)
summary(rules.ii)

# Task 4 part (f)
plot(rules,
     method = "graph",
     interactive = TRUE,
     shading = NA)
