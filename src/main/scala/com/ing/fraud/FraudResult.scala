package com.ing.fraud

import com.ing.data.Event
import org.joda.time.DateTime

/**
 * @param p -1 for no fraud,
 *          1 for fraud,
 *          0 for escalate,
 *          or between 0 and 1 for percentage chance
 */
class FraudResult(p: Float, m: Map[String, Any]) {
	val timestamp = DateTime.now().getMillis

	def getTime = timestamp
	def getParam = p

	def toJson(): String = {
		var temp = "{ \"timestamp\": \"" + timestamp + "\", \"p\": \"" + p + "\", "

		if (m != null) {
			m.keys.foreach(key => {
				val value: String = m.get(key).get.toString
				val quotes = if (value.startsWith("{")) "" else "\""

				temp += "\"" + key + "\": " + quotes + value + quotes + ", "
			})
		}

		temp.substring(0, temp.length - 2) + "}"
	}
}

object FraudResult {
	def apply(p: Float, e: Event, m: Map[String, Any]) = new FraudResult(p, Map("event" -> e.json) ++ m)
	def apply(p: Float, e: Event) = new FraudResult(p, Map("event" -> e.json))
	def apply(p: Float) = new FraudResult(p, null)
	def apply(p: Float, m: Map[String, Any]) = new FraudResult(p, m)
	def apply(f: FraudResult, m: Map[String, Any]) = new FraudResult(f.getParam, m)
}