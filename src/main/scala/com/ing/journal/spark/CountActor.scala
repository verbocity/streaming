package com.ing.journal.spark

import akka.actor.Actor
import com.ing.data.Event
import org.apache.spark.rdd.RDD

class CountActor extends Actor with HttpSend {
	def receive = {
		case StreamAction(rdd: RDD[Event]) =>
			send("count", "{ \"count\": \"" + rdd.count().toString + "\" }")
		case _ =>
	}
}