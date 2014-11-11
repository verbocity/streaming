package com.ing.journal.spark.traits

import com.ing.journal.spark.{StreamAction, ActorStack}
import org.joda.time.DateTime

trait Performance extends ActorStack {
	override def receive: Receive = if (enabled) {
		case x =>
			if (x.isInstanceOf[StreamAction]) {
				val start = DateTime.now().getMillis

				super.receive(x)

				val end = DateTime.now().getMillis
				val delta = end - start

				// ... Do something with delta here
				println("Time taken: " + delta)
			}
	} else {
		case x =>
			super.receive(x)
	}
}