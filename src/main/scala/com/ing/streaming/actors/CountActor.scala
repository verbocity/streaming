package com.ing.streaming.actors

import akka.actor.Actor
import com.ing.streaming.actors.traits.StreamAction
import com.ing.streaming.data.Event
import com.ing.streaming.spark.HttpSend
import org.apache.spark.rdd.RDD

class CountActor extends Actor with HttpSend {
	def receive = {
		case StreamAction(rdd: RDD[Event]) =>
			send("count", "{ \"count\": \"" + rdd.count().toString + "\" }")
		case _ =>
	}
}