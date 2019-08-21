# Streaming Test Harness, Docker Files

The Dockerfile sets up a Centos OS with the Java8 JDK, Spark, and Kafka.

The Spark download step is rather large, and will take 3-5 minutes to complete -- don't worry, the install is not hanging.

### Running the container

    docker build -t streaming .
    docker run --name streaming -it streaming

To see that it is working, in the Spark shell try:

    val rdd=sc.textFile("README.md")
    rdd.count()
