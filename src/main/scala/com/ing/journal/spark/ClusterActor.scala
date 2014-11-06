package com.ing.journal.spark

import com.ing.data.Event

class ClusterActor() extends StreamActor {
	override def receive = {
		case StreamAction(rdd) =>
			// Train the model on the incoming data stream
		case EventAction(e: Event) =>
			// Classify incoming transactions in a cluster
		case _ =>
	}
}