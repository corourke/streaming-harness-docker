FROM openjdk:8

RUN mkdir /usr/src/myapp/
COPY ./target/scan-generator-1.0.jar /usr/src/myapp
WORKDIR /usr/src/myapp

ENV KAFKA_BOOTSTRAP_SERVERS localhost:9092
ENV PRODUCER_INSTANCE "01"

CMD ["java", "-jar", "scan_generator-1.0.jar"]
