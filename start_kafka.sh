$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties &
sleep 10
$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties &
