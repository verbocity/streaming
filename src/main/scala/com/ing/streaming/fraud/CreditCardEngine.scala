package com.ing.streaming.fraud

import com.ing.streaming.data.Event
import scala.concurrent.duration.Duration

class CreditCardEngine extends FraudEngine {
	override def initialize(params: Map[String, Any]) = {

	}

	override def setTimeOut(t: Duration) = {

	}

	override def ask(e: Event): FraudResult = {
		FraudResult(1)
	}
}