package com.kinetica.scan_generator;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.String;

import net.andreinc.mockneat.types.enums.StringType;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.andreinc.mockneat.MockNeat;

import static java.lang.Runtime.*;

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


        // Latch for the producer thread
        CountDownLatch latch = new CountDownLatch(1);

        // Create the producer runnable
        logger.info("Creating producer thread");
        Runnable myProducerRunnable = new ProducerRunnable(
                bootstrapServers,
                topic,
                instance,
                latch
        );

        // Start the producer thread
        Thread myThread = new Thread(myProducerRunnable);
        myThread.start();

        // Add a shutdown hook to stop the producer in an orderly fashion
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Caught shutdown hook");
            ((ProducerRunnable) myProducerRunnable).shutdown();
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

    class ProducerRunnable implements Runnable {
        private KafkaProducer<String, String> producer;
        private CountDownLatch latch;
        private Boolean runnable = true;
        private String topic;
        private String instance;
        private Logger logger = LoggerFactory.getLogger(ProducerRunnable.class.getName());

        ProducerRunnable(String bootstrapServers,
                         String topic,
                         String instance,
                         CountDownLatch latch) {
            this.latch = latch;
            this.topic = topic;
            this.instance = instance;

            // create producer properties
            Properties properties = new Properties();
            properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            // possibly unnecessary
            properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, "4096");
            properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");
            properties.setProperty(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "3000");

            // create the producer
            this.producer = new KafkaProducer<String, String>(properties);

        }

        public void run() {
            MockNeat mock = MockNeat.threadLocal();

//            final class POS_Scan {
//                String location_id;
//                Long scan_ts;
//                String item_upc;
//                Integer unit_qty;
//            }

            try {
                Long lastTimestampReported = 0L;
                Long count=0L;

                while (runnable) {
                    String location = instance + "-" + String.format("%03d", mock.ints().range(1, 50).get());
                    Long timestamp = Instant.now().toEpochMilli();

                    String value = mock.csvs()
                            .column(location)
                            .column(timestamp)
                            .column(mock.strings().size(11).type(StringType.NUMBERS)) // TODO: Use real UPC numbers
                            .column(mock.ints().range(1, 3)) // TODO: use better qty formula
                            .accumulate(1, "\n")
                            .get();

                    String key = instance; // region

                    // create a producer record
                    ProducerRecord<String, String> record =
                            new ProducerRecord<String, String>(topic, key, value);

                    // send data - asynchronous
                    producer.send(record);

                    // flush() -- do not use for live testing, radically slows things down
                    //producer.flush();

                    // Code to report how many rows have been output
                    if (timestamp - lastTimestampReported >= 1000 ) {
                        System.out.println("Count (instance: " + instance + "): " + count);
                        lastTimestampReported = timestamp;
                    }
                    count++;
                }
            } catch (Exception e) {
                logger.error("Exception in Producer thread: " + e);
            } finally {
                producer.close();
                latch.countDown();
            }
        }

        public void shutdown() {
            logger.info("Stopping producer");
            runnable = false;
        }

    }


}





