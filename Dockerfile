FROM centos

MAINTAINER cameron.orourke@gmail.com

# SPARK
ENV SPARK_PROFILE 2.3
ENV SPARK_VERSION 2.3.3
ENV HADOOP_PROFILE 2.7
ENV HADOOP_VERSION 2.7.0

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


# Remove too many log messages from Spark
WORKDIR $SPARK_HOME/conf
RUN cp log4j.properties.template log4j.properties
RUN sed -i '/log4j.rootCategory/ s/INFO/ERROR/' log4j.properties

ENV PATH $PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin

EXPOSE 4040

RUN yum -y clean all

WORKDIR $SPARK_HOME

CMD ["spark-shell"]
