FROM openjdk:8

WORKDIR /usr/local/src

COPY ./target/scan-generator-1.0.jar ./


ENV KAFKA_BOOTSTRAP_SERVERS localhost:9092
ENV PRODUCER_INSTANCE "01"

ENTRYPOINT exec java -jar /usr/local/src/scan-generator-1.0.jar