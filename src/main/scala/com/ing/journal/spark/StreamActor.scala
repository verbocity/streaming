package com.ing.journal.spark

import akka.actor.Actor
import org.apache.spark.rdd.RDD
import com.ing.data.Event
import org.joda.time.DateTime

case class StartTiming(rdd: RDD[Event])
case class StopTiming()
case class StreamAction(rdd: RDD[Event])
case class EventAction(e: Event)

abstract class StreamActor extends Actor {
	var start: Long = 0
	var count: Long = 0

	def startTiming(rdd: RDD[Event]) = {
		start = DateTime.now().getMillis
		count = rdd.count()
	}

	def stopTiming() = {
		val end = DateTime.now().getMillis
		val time = end - start
		start = 0

		if (time > 0) {
			println(super.getClass().getSimpleName() + ": processed " + count
				+ " events in " + time
				+ " ms (" + (count / (time.toFloat / 1000)) + " events/sec).")
		}
	}
}