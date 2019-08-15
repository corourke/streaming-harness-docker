package com.github.corourke.kafka_data_genererator;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.andreinc.mockneat.MockNeat;


public class DataProducer {

	public static void main(String[] args) {
		final String bootstrapServers = getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
		final String instance = getenv("PRODUCER_INSTANCE", "01");
		
		final AtomicInteger state = new AtomicInteger(2);
			// 0 EXIT
			// 1 RUNNING
			// 2 WAITING
		
		final Logger logger = LoggerFactory.getLogger(DataProducer.class);
		
		// create producer properties
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		// create the producer
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		
		
		// Implements a thread to read commands from a topic
		final class StatusPoller implements Runnable {
			public void run(){
				// This is a consumer just for receiving commands
				KafkaConsumer<String, String> consumer = getConsumer(bootstrapServers, instance);
				
				loop: while(true) {
					ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(1000)); 
					consumer.commitAsync();
					for(ConsumerRecord<String,String> record : records) {
						String command = record.value().split(" ", 2)[0];
						logger.info("Command recieved: " + command);
						
						switch(command) {
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
							logger.error("Unexpected command recieved: " + command);
						}
					}
				}
				logger.info("Status poller exiting");
		   } 
		}
		
		Thread poller = new Thread(new StatusPoller ());
		poller.start();
		
		MockNeat mock = MockNeat.threadLocal();
		
		final class POS_Scan {
		    String location_id;
		    String scan_datetime;
		    String item_upc;
		    Integer unit_qty;
		}
		
		
		// Main loop -- will wait for START
		while(state.get() != 0) { // 0 = EXIT
			
			// Don't waste CPU if we are waiting
			if(state.get() == 2) {
				logger.info("Waiting...");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					producer.flush();
					e.printStackTrace();
				}
			}
			
			if(state.get() == 1) { // 1 = RUNNING
				for (int i=0; i<10; i++) {
					
					String topic = "pos_scans";
					
					String location = instance + "-" + String.format("%03d", mock.ints().range(1, 50).get());
					
					String csv = mock.csvs()
					  .column(location)
					  .column(mock.names().first())
					  .column(mock.names().last())
					  .column(mock.emails())
					  .accumulate(1, "\n")
					  .get();
					
					String value = csv;
					String key = instance; // region
					
					
					// create a producer record
					ProducerRecord<String, String> record = 
							new ProducerRecord<String, String>(topic, key, value);
					
					// send data - asynchronous
					producer.send(record);
					
					// flush
					producer.flush();
				}
			} // if RUNNING
		} // while
		
		// flush and close
		producer.close(); 

	}
	
	
	static KafkaConsumer<String, String> getConsumer(String bootstrapServers, String instance) {
		String groupId = instance;
		String topic = "generator_commands";
		
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		
		KafkaConsumer<String, String> consumer =
				new KafkaConsumer<String, String>(properties);
		consumer.subscribe(Collections.singleton(topic));
		
		return consumer;
	}
	
	
	static String getenv(String var, String default_value) {
		String value = System.getenv(var);
		if (value == null) { 
			return default_value;
		} else {
			return value;
		}
	}

}
