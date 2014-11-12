package com.ing.streaming.actors

import akka.actor.Actor
import com.ing.streaming.actors.traits.StreamAction
import com.ing.streaming.data.Event
import org.apache.spark.rdd.RDD

class PredictionActor() extends Actor {
	override def receive = {
		case StreamAction(rdd: RDD[Event]) =>
		case _ =>
	}
}