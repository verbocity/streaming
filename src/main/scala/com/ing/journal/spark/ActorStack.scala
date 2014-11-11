package com.ing.journal.spark

import akka.actor.Actor
import com.ing.journal.spark.traits.{Metrics, Logging, Performance}
import org.apache.spark.rdd.RDD
import com.ing.data.Event
import org.joda.time.DateTime

case class StreamAction(rdd: RDD[Event])
case class EventAction(e: Event)

trait ActorStack extends Actor {
	val enabled = true
	def Receive: Receive

	def receive: Receive = {
		case x =>
			if (Receive.isDefinedAt(x)) {
				Receive(x)
			} else {
				unhandled(x)
			}
	}
}