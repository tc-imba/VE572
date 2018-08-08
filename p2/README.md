## Task 4

This task is about using Spark to run a k-means algorithm. 
We use R to connect to Spark (sparklyr) and use the h2o package in R to analyze the data.

In order to connect these libraries, we use the `rsparkling` package. 
The rsparkling R package is an extension package for sparklyr that creates an R front-end 
for the Sparkling Water package from H2O. This provides an interface to H2O's high performance, 
distributed machine learning algorithms on Spark, using R.

### Install the dependencies

First, you should have jdk8 installed to use `Spark`.

Then all of the commands are run in R console

It is advised to remove previously installed H2O versions and install H2O dependencies. 
The command bellow can be used for this.

```r
# The following two commands remove any previously installed H2O packages for R.
if ("package:h2o" %in% search()) { detach("package:h2o", unload=TRUE) }
if ("h2o" %in% rownames(installed.packages())) { remove.packages("h2o") }

# Install packages H2O depends on
pkgs <- c("methods", "statmod", "stats", "graphics", "RCurl", "jsonlite", "tools", "utils")
for (pkg in pkgs) {
    if (! (pkg %in% rownames(installed.packages()))) { install.packages(pkg) }
}
```

Install the three packages needed.

```r
install.packages("sparklyr")
install.packages("h2o", type = "source", repos = "http://h2o-release.s3.amazonaws.com/h2o/rel-wright/4/R")
install.packages("rsparkling")
```

And because some network problems in China, you may fail to install to `sparkling-water` plugin
automatically with maven, so we use a native version of it. Download it 
[here](http://h2o-release.s3.amazonaws.com/sparkling-water/rel-2.3/10/index.html).

The path to the Sparkling Water jar file is: 
`sparkling-water-2.3.10/assembly/build/libs/sparkling-water-assembly_*.jar`.

Copy the file to your R script directory and it will works.

### Running

Set the working directory as the R script directory, 
and make sure you have all of the dependencies installed.
You will get a k-means result after a few minutes.
