package com.ing.journal.spark

import com.ing.data.Event
import org.apache.spark.rdd.RDD

class PredictionActor() extends StreamActor {
	override def receive = {
		case StreamAction(rdd: RDD[Event]) =>
		case _ =>
	}
}