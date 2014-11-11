package com.ing.journal.spark

import akka.actor.Actor
import com.ing.data.Event
import org.apache.spark.rdd.RDD

class PredictionActor() extends Actor {
	override def receive = {
		case StreamAction(rdd: RDD[Event]) =>
		case _ =>
	}
}