package kafka

import java.util.Properties

import org.apache.kafka.clients.producer.KafkaProducer

object KafkaProducer {
  val props = new Properties
  val brokers = "localhost:9092"
  props.put("bootstrap.servers", brokers)
  props.put("client.id", "Producer")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

}
