package com.kinetica.corourke;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.lang.String;
import java.util.concurrent.atomic.AtomicInteger;

import net.andreinc.mockneat.types.enums.StringType;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.andreinc.mockneat.MockNeat;

public class ScanDataGenerator {

    public static void main(String[] args) {
        new ScanDataGenerator().run();
    }

    private ScanDataGenerator() {
    }

    private void run() {
        final Logger logger = LoggerFactory.getLogger(ScanDataGenerator.class);

        String bootstrapServers = getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        String instance = getenv("PRODUCER_INSTANCE", "01");
        String topic = "pos_scans";

        final AtomicInteger state = new AtomicInteger(2);
        // 0 EXIT
        // 1 RUNNING
        // 2 WAITING
        final AtomicInteger rate = new AtomicInteger(100);

        // Start command poller -- reads start, stop, rate commands
        Runnable commandPoller = new CommandPoller(bootstrapServers,
                "scan_generator_commands", instance, state, rate);
        Thread pollerThread = new Thread(commandPoller);
        pollerThread.start();

        // Latch to wait for the producer thread to flush and return
        CountDownLatch latch = new CountDownLatch(1);

        // Create the producer runnable
        logger.info("Creating producer thread");
        Runnable scanProducer = new ScanProducer(
                bootstrapServers,
                topic,
                state,
                rate,
                instance,
                latch
        );

        // Start the producer thread
        Thread myThread = new Thread(scanProducer);
        myThread.start();

        // Add a shutdown hook to stop the producer in an orderly fashion
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Caught shutdown hook");
            ((ScanProducer) scanProducer).shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                logger.error("Error in shutdown hook: " + e);
            }
            logger.info("Application is closing");
        }));
    }

    private String getenv(String var, String default_value) {
        String value = System.getenv(var);
        if (value == null) {
            return default_value;
        } else {
            return value;
        }
    }
}





