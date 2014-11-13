package com.ing.streaming.spark

import akka.actor.{ActorRef, ActorSystem, Props}
import com.ing.streaming.actors._
import com.ing.streaming.data.Event
import com.ing.streaming.actors.traits.{EventAction, StreamAction}
import org.apache.log4j.{Level, Logger}
import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming._

object JournalListener extends App {
	Logger.getRootLogger().setLevel(Level.ERROR)

	// Set up NL cities distances table
	//EventHistory.initialize()

	val baseWindow = Seconds(1)
	val listenOnPort = 1357
	val system = ActorSystem()
	val actors = createActorList()
	val conf = new SparkConf()
		.setMaster("local[8]")
		/*.setMaster("mesos://zk://192.168.2.102:2181/mesos")
		.set("spark.executor.uri", "http://192.168.2.101:8000/spark-1.1.0-bin-hadoop2.4.tgz")
		.set("spark.mesos.executor.home", "/tmp")
		*/
		.setAppName("JournalListener")
	val receiver = new LineReceiver(listenOnPort)
	val ssc = new StreamingContext(conf, baseWindow)

	// Tell Spark Streaming to keep the RDD's around for at
	// least X seconds so that all actors have time to
	// perform their actions on the stream.
	// Otherwise, the RDD gets discarded immediately.
	// ssc.remember(Seconds(1))

	val stream = ssc.receiverStream(receiver)

	// Create sliding window with window size and repetition interval
	val windowed = stream.window(Seconds(10), Seconds(1))

	// Event processing for individual events
	receiver.setListenFunction((e: Event) => {
		for (actor <- actors) {
			actor ! EventAction(e)
		}
	})

	// Microbatching with Spark Streaming
	windowed.foreachRDD((rdd: RDD[Event]) => {
		for (actor <- actors) {
			actor ! StreamAction(rdd)
		}
	})

	try {
		ssc.start()
		ssc.awaitTermination()
	} catch {
		case _: Exception =>
		// Suppress useless Spark errors
	}

	def createActorList(): List[ActorRef] = {
		List(
			system.actorOf(Props(new StatisticsActor())),
			system.actorOf(Props(new RegionActor())),
			//system.actorOf(Props(new FraudActor())),
			//system.actorOf(Props(new GroupActor())),
			//system.actorOf(Props(new ClusterActor())),
			//system.actorOf(Props(new PredictionActor())),
			//system.actorOf(Props(new CountActor())),
			system.actorOf(Props(new WordCloudActor()))
			//system.actorOf(Props(new TravelActor()))
		)
	}
}