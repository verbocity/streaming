package com.ing.fraud

import java.util.Random

import com.ing.data.Event
import com.ing.journal.spark.{StreamActor, EventAction, HttpSend}
import org.joda.time.DateTime

class FraudActor() extends StreamActor with HttpSend {
	var previousEventSameAccount: Map[String, Event] = _
	val customRules: List[Event => FraudResult] = initializeRules()
	val engineManager = new FraudEngineManager()

	def receive = {
		case EventAction(event: Event) =>
			if (new Random().nextFloat() < 0.01) {
				customRules.foreach(rule => send("fraud", rule(event).toJson))
				val result = engineManager.thresholdVote(event, 0.7f)

				send("fraud", result.toJson)
			}
		case _ =>
	}

	def initializeRules() = {
		val map = Map("type" -> "customRule")

		List(
			(e: Event) => { FraudResult(if (e.time < 10) 1 else -1, e, map) },
			(e: Event) => { FraudResult(if (e.getField("amount").toFloat > 1000.0f) 1 else -1, e, map) },
			(e: Event) => { FraudResult(if (e.getField("city") == "amsterdam") 1 else -1, e, map) },
			(e: Event) => { FraudResult(if (e.getField("status").toInt == 76) 1 else -1, e, map) }
		)
	}
}