# streaming-harness-docker
Dockerized test harness for kafka-spark-cassandra. 

Main project:

* scan-generator -- generates POS scans to Kafka topic. Takes start/stop commands from another Kafka topic. 


Other projects:

* docker-images -- start on Dockerfile for Centos, Java8, Spark and Kafka
* JMeterModules -- 
* k8config -- Kubernetes 
* kafka-data-consumer, kafka-data-generator -- simple/sample kafka examples
* kperf_app -- start on a Meteor app to front-end the test harness, only checks services so far
* mockaroo_data_downloader -- shell scripts that download Mockaroo CSV files and upload them to AWS S3
* static-data-generator -- generates store and item master data (although actual store data generated from Mockaroo)
* test-data -- generated and static data

Mockaroo Schemas and Datasets:

* pos_scans -- generates pos scans (latest schema as used in scan-generator)
* region_scans -- used by the mockaroo_data_downloader (obsolete)
* stores -- generates the store master that is currently being used (did not use static-data-generator for stores)

* item_master -- old 2,000 item master used by region_scans
* new_item_master -- the new generated 32,000 item master used by pos_scans
