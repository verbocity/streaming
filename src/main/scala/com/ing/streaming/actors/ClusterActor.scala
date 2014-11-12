package com.ing.streaming.actors

import akka.actor.Actor
import com.ing.streaming.actors.traits.{EventAction, StreamAction}
import com.ing.streaming.data.Event

class ClusterActor() extends Actor {
	override def receive = {
		case StreamAction(rdd) =>
			// Train the model on the incoming data stream
		case EventAction(e: Event) =>
			// Classify incoming transactions in a cluster
		case _ =>
	}
}