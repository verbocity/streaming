package com.ing.journal.spark.traits

import com.ing.journal.spark.ActorStack

trait Logging extends ActorStack {
	override def receive: Receive = if (enabled) {
		case x =>
			// before action
			super.receive(x)
			// after action
	} else {
		case x =>
			super.receive(x)
	}
}