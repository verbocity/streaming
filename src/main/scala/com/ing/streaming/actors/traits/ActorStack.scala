package com.ing.streaming.actors.traits

import akka.actor.Actor
import com.ing.streaming.data.Event
import org.apache.spark.rdd.RDD

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