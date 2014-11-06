package com.ing.fraud

import com.ing.data.Event
import org.joda.time.DateTime

class FraudEngineManager {
	val engines = initializeEngines()

	def initializeEngines(): List[FraudEngine] = {
		List(new RiskShieldEngine(),    // always returns -1
			 new CreditCardEngine(),    // always returns 1
			 new RandomEngine(),        // returns random 1 or -1
			 new RandomEngine(),
			 new RandomEngine(),
			 new RandomEngine(),
			 new RandomEngine())
	}

	/**
	 * Return positive when a percentage of engines
	 * greater than the threshold says it is fraud
	 * @param e the event to process
	 * @return a FraudResult
	 */
	def thresholdVote(e: Event, t: Float): FraudResult = {
		val map = engines.map(engine => engine.ask(e))
		val count: Int = map.count(_.getParam == 1.0f)
		val percentage = count / engines.size.toFloat
		val props = Map("voteType" -> "thresholdVote", "threshold" -> t,
			"event" -> e.json)
		FraudResult(if (percentage >= t) 1 else -1, props)
	}

	/**
	 * If the majority of the engines thinks it is fraud,
	 * then say it is fraud.
	 * @param e the event to process
	 * @return a FraudResult
	 */
	def majorityVote(e: Event): FraudResult = {
		thresholdVote(e, 0.5f)
	}

	/**
	 * If all engines think it is fraud, then say it is fraud.
	 * @param e The event to process
	 * @return a FraudResult
	 */
	def allVote(e: Event): FraudResult = {
		thresholdVote(e, 1.0f)
	}

	/**
	 * Return true when any engine says it is fraud.
	 * @param e the event to process
	 * @return a FraudResult
	 */
	def anyVote(e: Event): FraudResult = {
		thresholdVote(e, 1 / engines.size.toFloat)
	}

	/**
	 * Return the first answer that comes back from
	 * any engine. Take the one with the shortest timeout
	 * @param e the event to process
	 * @return a FraudResult
 	 */
	def firstComeVote(e: Event): FraudResult = {
		FraudResult(
			engines.map(engine => engine.ask(e))
			.sortBy(_.getTime).head,
			Map("voteType" -> "firstComeVote"))
	}
}