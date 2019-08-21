FROM centos

MAINTAINER cameron.orourke@gmail.com

# SPARK
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

# Install Kafka
WORKDIR /opt
ENV KAFKA_HOME /opt/kafka
RUN wget --no-verbose "http://apache.mirrors.lucidnetworks.net/kafka/$KAFKA_VERSION/kafka_$SCALA_VERSION-$KAFKA_VERSION.tgz" -O "/tmp/kafka_$SCALA_VERSION-$KAFKA_VERSION.tgz"
RUN gunzip -c "/tmp/kafka_$SCALA_VERSION-$KAFKA_VERSION.tgz" | tar x -C /opt/
RUN ln -s /opt/kafka_$SCALA_VERSION-$KAFKA_VERSION $KAFKA_HOME
RUN rm "/tmp/kafka_$SCALA_VERSION-$KAFKA_VERSION.tgz"
ENV PATH $PATH:$KAFKA_HOME/bin
RUN sed -i '/log4j.rootCategory/ s/INFO/WARN/' $KAFKA_HOME/config/log4j.properties

# TODO: Need to add zookeeper and kafka data storage configuration

RUN yum -y clean all

EXPOSE 2181 2888 3888 8080

CMD ["bash"]

