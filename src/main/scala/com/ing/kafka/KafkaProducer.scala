package com.ing.kafka

import java.util.{Date, Properties}
import kafka.javaapi.producer.Producer
import kafka.producer.{KeyedMessage, ProducerConfig}
import Constants._
import kafka.producer
import org.joda.time.DateTime
import scala.collection.JavaConversions._
import kafka.Kafka
import scala.util.Random
import scala.collection.JavaConversions._
import joptsimple._
import java.util.{Properties, UUID}
import java.io._
import kafka.common._
import kafka.message._
import kafka.serializer._
import java.util.Properties

object KafkaProducer extends App {
	val random = new Random()
	val props = new Properties()

	props ++= Map(
		"serializer.class" -> "com.ing.kafka.ImpressionLogEncoder",
		"metadata.broker.list" -> "127.0.0.1:9093"
	)

	val config = new ProducerConfig(props)
	val producer = new Producer[String, ImpressionLog](config)

	println("Sending messages...")
	var i = 0

	while (true) {
		val timestamp = System.currentTimeMillis()
		val publisher = Publishers(random.nextInt(NumPublishers))
		val advertiser = Advertisers(random.nextInt(NumAdvertisers))
		val website = s"website_${random.nextInt(Constants.NumWebsites)}.com"
		val cookie = s"cookie_${random.nextInt(Constants.NumCookies)}"
		val geo = Geos(random.nextInt(Geos.size))
		val bid = math.abs(random.nextDouble()) % 1
		val log = ImpressionLog(timestamp, publisher, advertiser, website, geo, bid, cookie)
		producer.send(new KeyedMessage[String, ImpressionLog](Constants.KafkaTopic, log))

		i += 1
		if (i % 10000 == 0) {
			println(s"Sent $i messages!")
		}
	}
}