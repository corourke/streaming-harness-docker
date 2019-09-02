package com.kinetica.ktest;

import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;
import java.lang.String;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;

public class ScanDataGenerator {

    public static void main(String[] args) throws Exception {
        new ScanDataGenerator().run();
    }

    private ScanDataGenerator() throws Exception {

        // Register our Controller MBean with the Agent
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = null;
        try {
            objectName = new ObjectName("com.kinetica.ktest:type=basic,name=Controller");
            Controller mbean = new Controller();
            mbs.registerMBean(mbean, objectName);
        } catch (MalformedObjectNameException
                | InstanceAlreadyExistsException
                | MBeanRegistrationException
                | NotCompliantMBeanException e) {
            e.printStackTrace();
        }

    }

    private void run() throws Exception {
        final Logger logger = LoggerFactory.getLogger(ScanDataGenerator.class);

        // Set up Kafka producer
        String bootstrapServers = getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        String instance = getenv("PRODUCER_INSTANCE", "01");
        String topic = "pos_scans";


        // Start command poller -- reads start, stop, rate commands
        Runnable commandPoller = new CommandPoller(bootstrapServers,
                "scan_generator_commands", instance);
        Thread pollerThread = new Thread(commandPoller);
        pollerThread.start();

        // Latch to wait for the producer thread to flush and return
        CountDownLatch latch = new CountDownLatch(1);

        // Create the producer runnable
        logger.info("Creating producer thread");
        Runnable scanProducer = new ScanProducer(
                bootstrapServers,
                topic,
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





