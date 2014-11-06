package com.ing.journal.spark

import com.ing.data.Event
import org.apache.spark.rdd.RDD
import org.joda.time.DateTime
import scala.collection.mutable.{Map => mMap}

class StatisticsActor extends StreamActor with HttpSend {
	var peakTranCount: Long = 0
	var tranCountTotal: Long = 0
	var sumAmountTotal: Long = 0

	override def receive = {
		case StreamAction(rdd: RDD[Event]) =>
			super.startTiming(rdd)

			calculateStatistics(rdd)

			super.stopTiming()
		case EventAction(transaction) =>
		case _ =>
	}

	def calculateStatistics(rdd: RDD[Event]) {
		val result = mMap.empty[String, Any]
		val tranCountThisWindow = rdd.count()
		val list = rdd.collect()

		tranCountTotal += tranCountThisWindow
		result.put("tranCountThisWindow", tranCountThisWindow)
		result.put("tranCountTotal", tranCountTotal)

		if (tranCountThisWindow > 0) {
			val firstTime: Long = list(0).time

			result.put("currentTime", firstTime)

			val sumAmountThisWindow = rdd.map(e => {
				e.getField("amount").toFloat
			}).reduce(_ + _)

			result.put("sumAmountThisWindow", sumAmountThisWindow)

			sumAmountTotal += sumAmountThisWindow.toInt
			result.put("sumAmountTotal", sumAmountTotal)

			val avgAmountThisWindow = sumAmountThisWindow / tranCountThisWindow
			result.put("avgAmountThisWindow", avgAmountThisWindow)

			val lastTime = list(list.size - 1).time
			val diff = lastTime - firstTime

			if (diff > 0) {
				val avgTransPerSecThisWindow = tranCountThisWindow / diff.toFloat
				result.put("avgTransPerSecThisWindow", avgTransPerSecThisWindow)
			}

			if (tranCountThisWindow > peakTranCount) peakTranCount = tranCountThisWindow.toInt
			result.put("peakTranCount", peakTranCount)

			val mostFrequentCategory = 2
			result.put("mostFrequentCategory", mostFrequentCategory)

			println("Sending stats at " + DateTime.now())
			send("stats", JsonResult.mapToJson(result.toMap))
		}
	}
}