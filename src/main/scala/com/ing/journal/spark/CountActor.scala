package com.ing.journal.spark

import com.ing.data.Event
import org.apache.spark.rdd.RDD

class CountActor extends StreamActor with HttpSend {
	def receive = {
		case StreamAction(rdd: RDD[Event]) =>
			super.startTiming(rdd)
			send("count", "{ \"count\": \"" + rdd.count().toString + "\" }")
			super.stopTiming()
		case _ =>
	}
}