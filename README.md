The Dockerfile sets up a Centos OS with the Java8 JDK and Spark.

The Spark `wget` download is rather large, and will take 3-5 minutes to download.

### Running the container

    docker build -t streaming .
    docker run --name streaming -it streaming

To see that it is working, in the Spark shell try:

    val rdd=sc.textFile("README.md")
    rdd.count()
# streaming-harness-docker
