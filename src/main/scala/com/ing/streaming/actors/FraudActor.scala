package com.ing.streaming.actors

import java.util.Random

import akka.actor.Actor
import com.ing.streaming.actors.traits.EventAction
import com.ing.streaming.data.Event
import com.ing.streaming.fraud.{FraudEngineManager, FraudResult}
import com.ing.streaming.spark.HttpSend

class FraudActor() extends Actor with HttpSend {
	var previousEventSameAccount: Map[String, Event] = _
	val customRules: List[Event => FraudResult] = initializeRules()
	val engineManager = new FraudEngineManager()
	
	def receive = {
		case EventAction(event: Event) =>
			if (event.getField("amount").toFloat > 1000.0f) send("fraud", event.getJsObject.toString)

		    //customRules.foreach(rule => send("fraud", rule(event).toJson))
				//val result = engineManager.thresholdVote(event, 0.7f)
				//send("fraud", result.toJson)
		case _ =>
	}

	def initializeRules() = {
		val map = Map("type" -> "customRule")

		List(
			//(e: Event) => { FraudResult(if (e.time < 10) 1 else -1, e, map) },
			(e: Event) => { FraudResult(if (e.getField("amount").toFloat > 100.0f) 2 else -1, e, map) },
			//(e: Event) => { FraudResult(if (e.getField("city") == "amsterdam") 3 else -1, e, map) },
			(e: Event) => { FraudResult(if (e.getField("status").toInt == 76) 4 else -1, e, map) }
		)
	}
}