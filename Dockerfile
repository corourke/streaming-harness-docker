FROM centos

MAINTAINER cameron.orourke@gmail.com

# SPARK
ENV SPARK_PROFILE 2.3
ENV SPARK_VERSION 2.3.3
ENV HADOOP_PROFILE 2.7
ENV HADOOP_VERSION 2.7.0
ENV KAFKA_VERSION 2.3.0
ENV SCALA_VERSION 2.11

RUN yum install -y \
   wget \
   bzip2 \
   tar \
   unzip \
   git \
   java-1.8.0-openjdk \
   java-1.8.0-openjdk-devel

ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk/

# Install Spark
# Separate run statements for debugging / caching -- should probably combine some
ENV SPARK_HOME /opt/spark
RUN wget --no-verbose "http://apache.mirrors.lucidnetworks.net/spark/spark-$SPARK_VERSION/spark-$SPARK_VERSION-bin-hadoop$HADOOP_PROFILE.tgz" -O "/tmp/spark-$SPARK_VERSION-bin-hadoop$HADOOP_PROFILE.tgz"
RUN gunzip -c "/tmp/spark-$SPARK_VERSION-bin-hadoop$HADOOP_PROFILE.tgz" | tar x -C /opt/
RUN ln -s /opt/spark-$SPARK_VERSION-bin-hadoop$HADOOP_PROFILE $SPARK_HOME
RUN rm "/tmp/spark-$SPARK_VERSION-bin-hadoop$HADOOP_PROFILE.tgz"
ENV PATH $PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin
EXPOSE 4040

# Remove too many log messages from Spark
WORKDIR $SPARK_HOME/conf
RUN cp log4j.properties.template log4j.properties
RUN sed -i '/log4j.rootCategory/ s/INFO/ERROR/' log4j.properties

# Kafka
WORKDIR /opt
ENV KAFKA_HOME /opt/kafka
RUN wget --no-verbose "http://apache.mirrors.lucidnetworks.net/kafka/$KAFKA_VERSION/kafka_$SCALA_VERSION-$KAFKA_VERSION.tgz" -O "/tmp/kafka_$SCALA_VERSION-$KAFKA_VERSION.tgz"
RUN gunzip -c "/tmp/kafka_$SCALA_VERSION-$KAFKA_VERSION.tgz" | tar x -C /opt/
RUN ln -s /opt/kafka_$SCALA_VERSION-$KAFKA_VERSION.tgz $KAFKA_HOME
RUN rm "/tmp/kafka_$SCALA_VERSION-$KAFKA_VERSION.tgz"
ENV PATH $PATH:$KAFKA_HOME/bin

RUN yum -y clean all

CMD ["spark-shell"]
