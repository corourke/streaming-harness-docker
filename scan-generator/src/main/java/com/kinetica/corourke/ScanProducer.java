package com.kinetica.corourke;

import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.types.enums.StringType;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ScanProducer implements Runnable {
    private KafkaProducer<String, String> producer;
    private CountDownLatch latch;
    private String topic;
    private AtomicInteger state;
    private AtomicInteger rate;
    private String instance;
    private Logger logger = LoggerFactory.getLogger(ScanProducer.class.getName());

    ScanProducer(String bootstrapServers,
                 String topic,
                 AtomicInteger state,
                 AtomicInteger rate,
                 String instance,
                 CountDownLatch latch) {
        this.latch = latch;
        this.topic = topic;
        this.instance = instance;
        this.state = state;
        this.rate = rate;

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
            Long lastTimestamp = Instant.now().toEpochMilli();
            Long count=0L;
            Long last_count=0L;

            while (state.get() != 0) {
                // Don't waste CPU if we are waiting
                if(state.get() == 2) {
                    // TODO: THis needs to be sent to instrumentation instead
                    logger.info("Waiting...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        producer.flush();
                    }
                }

                if(state.get() ==1) { // 1 = RUNNING

                    String location = instance + "-" + String.format("%03d", mock.ints().range(1, 50).get());
                    Long timestamp = Instant.now().toEpochMilli();

                    // TODO: This is the only non-generic part of this class -- make a callback
                    String value = mock.csvs() // TODO: generate JSON instead
                            .column(location) // TODO: use store location
                            .column(timestamp)
                            .column(mock.strings().size(10).type(StringType.NUMBERS).prepend("1")) // TODO: Use real UPC numbers
                            .column(mock.ints().range(1, 3)) // TODO: use better qty formula
                            .accumulate(1, "\n")
                            .get();

                    String key = instance; // region

                    // create a producer record
                    ProducerRecord<String, String> record =
                            new ProducerRecord<String, String>(topic, key, value);

                    // send data - asynchronous
                    producer.send(record);
                    count++;

                    // flush() -- do not use for live testing, radically slows things down
                    //producer.flush();k

                    // Code to report how many rows have been output and rate limiting
                    int time_elapsed = (int) (timestamp - lastTimestamp);
                    if (time_elapsed >= 1000 || (count - last_count) >= rate.get()) {
                        // TODO: This needs to be output to the instrumentation device instead
                        logger.info("Count (instance: " + instance + "): " + count + " Rate: " + (count - last_count));
                        if ((time_elapsed) < 1000) {
                            Thread.sleep(1000 - (time_elapsed));
                        }
                        lastTimestamp = Instant.now().toEpochMilli();
                        last_count = count;
                    }
                } // IF RUNNING
            } // WHILE NOT EXIT
        } catch (Exception e) {
            logger.error("Exception in Producer thread: " + e);
        } finally {
            producer.close();
            latch.countDown();
        }
    }

    public void shutdown() {
        logger.info("Stopping producer");
        state.set(0); // EXIT
    }

}
