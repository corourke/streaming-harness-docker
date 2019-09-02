package com.kinetica.ktest;

import com.google.gson.Gson;
import com.opencsv.CSVReaderHeaderAware;
import net.andreinc.mockneat.MockNeat;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.JMX;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


public class ScanProducer implements Runnable {
    private KafkaProducer<String, String> producer;
    private CountDownLatch latch;
    private String topic;
    private String instance;
    private Logger logger = LoggerFactory.getLogger(ScanProducer.class.getName());
    private ArrayList<String> items = new ArrayList<>(32000);

    private ControllerMBean controller;


    ScanProducer(String bootstrapServers,
                 String topic,
                 String instance,
                 CountDownLatch latch) {
        this.latch = latch;
        this.topic = topic;
        this.instance = instance;

        // Read in the UPC codes from the item_master file
        load_csv(logger, items);


        // create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // possibly unnecessary
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, "4096");
        properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "2");
        properties.setProperty(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "1000");

        // create the producer
        this.producer = new KafkaProducer<String, String>(properties);

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


    // This bean is required by the gson library to produce the JSON output
    private static class POS_Scan {
        Integer store_id;
        Long scan_ts;
        String item_upc;
        Integer unit_qty;
    }

    public void run() {
        MockNeat mock = MockNeat.threadLocal();
        Gson gson = new Gson();

        try {
            Long lastTimestamp = Instant.now().toEpochMilli();
            Long count=0L;
            Long last_count=0L;

            while (controller.getState() != 0) {
                // Don't waste CPU if we are waiting
                if(controller.getState() == 2) {
                    // TODO: THis needs to be sent to instrumentation instead
                    logger.info("Waiting...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        producer.flush();
                    }
                }

                if(controller.getState() == 1) { // 1 = RUNNING
                    long timestamp = Instant.now().toEpochMilli();

                    // TODO: This is the only non-generic part of this class -- make a callback
                    POS_Scan pos_scan = new POS_Scan();

                    // Store number
                    // TODO: should probably check the seed data rather than assume
                    pos_scan.store_id = mock.ints().range(1234, 4233).get();

                    pos_scan.scan_ts = timestamp;

                    // Random UPC number from item_master
                    int index = (int)(Math.random() * items.size());
                    pos_scan.item_upc = items.get((int)(Math.random() * items.size()));

                    pos_scan.unit_qty = mock.probabilites(Integer.class)
                            .add(.5, 1)
                            .add(.25, 2)
                            .add(.15, 3)
                            .add(.10, 4).get();

                    String key = instance; // region
                    String value = gson.toJson(pos_scan);

                    // create a producer record
                    ProducerRecord<String, String> record =
                            new ProducerRecord<>(topic, value);

                    // send data - asynchronous
                    producer.send(record);
                    count++;

                    // flush() -- do not use for live testing, radically slows things down
                    //producer.flush();k

                    // Code to report how many rows have been output and rate limiting
                    int time_elapsed = (int) (timestamp - lastTimestamp);
                    if (time_elapsed >= 1000 || (count - last_count) >= controller.getRate()) {
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
        controller.setState(0); // EXIT
    }

    private static void load_csv(Logger logger, AbstractList list) {
        try {
            // TODO: Take output file from command line
            Reader reader = new FileReader("./src/main/resources/seed-data/item_master.csv");
            CSVReaderHeaderAware csvReader = new CSVReaderHeaderAware(reader);
            Map csvLine;

            // Read in each category from the CSV file
            while((csvLine = csvReader.readMap()) != null) {
                list.add(csvLine.get("ITEM_UPC"));
            }
            csvReader.close();
            reader.close();

        } catch (FileNotFoundException e) {
            logger.error("Can not find input CSV file. ", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
