package com.kinetica.ktest;

import java.lang.management.ManagementFactory;
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

import javax.management.*;

public class CommandPoller implements Runnable {
    private Logger logger = LoggerFactory.getLogger(CommandPoller.class);
    private String bootstrapServers = "localhost:9092";
    private String topic;
    private String instance;

    private int rate;
    private int state;

    private ControllerMBean controller;

    public CommandPoller(String _bs, String _topic, String _instance) {
        bootstrapServers = _bs;
        topic = _topic;
        instance = _instance;

        // Get the Controller bean
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            controller = JMX.newMBeanProxy(mbs,
                    new ObjectName("com.kinetica.ktest:type=basic,name=Controller"),
                    ControllerMBean.class, true);
        } catch ( MalformedObjectNameException e) {
            e.printStackTrace();
        }

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
                System.out.println("Command received: " + command);

                switch(command) {
                    case "RATE":
                        int new_rate = Integer.parseInt(fields[1]);
                        controller.setRate(new_rate);
                        break;
                    case "START":
                        controller.setState(1); // 1 = RUNNING
                        break;
                    case "STOP":
                        controller.setState(2); // 2 = WAITING
                        break;
                    case "EXIT":
                        controller.setState(0); // 0 = EXIT
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

