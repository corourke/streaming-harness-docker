package com.kinetica.corourke;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandPoller implements Runnable {
    private Logger logger = LoggerFactory.getLogger(CommandPoller.class);
    private String bootstrapServers = "localhost:9092";
    private String topic;
    private String instance;
    private AtomicInteger state;
    private AtomicInteger rate;

    public CommandPoller(String _bs, String _topic, String _instance, AtomicInteger _state, AtomicInteger _rate) {
        bootstrapServers = _bs;
        topic = _topic;
        instance = _instance;
        state = _state;
        rate = _rate;
    }

    @Override
    public void run() {
        // This is a consumer just for receiving commands
        KafkaConsumer<String, String> consumer = getConsumer(bootstrapServers, topic, instance);

        loop: while(true) {
            ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(1000));
            consumer.commitAsync();
            for(ConsumerRecord<String,String> record : records) {
                String[] fields = record.value().split(" ", 2);
                String command = fields[0];
                logger.info("Command received: " + command);

                switch(command) {
                    case "RATE":
                        int new_rate = Integer.parseInt(fields[1]);
                        rate.set(new_rate);
                        break;
                    case "START":
                        state.set(1); // 1 = RUNNING
                        break;
                    case "STOP":
                        state.set(2); // 2 = WAITING
                        break;
                    case "EXIT":
                        state.set(0); // 0 = EXIT
                        break loop;
                    default:
                        logger.error("Unexpected command received: " + command);
                }
            }
        }
        logger.info("Poller exiting");
    }

    private static KafkaConsumer<String, String> getConsumer(String bootstrapServers, String topic, String group) {

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, group);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        KafkaConsumer<String, String> consumer =
                new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Collections.singleton(topic));

        return consumer;
    }
}

