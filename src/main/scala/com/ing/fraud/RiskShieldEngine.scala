package com.ing.fraud

import com.ing.data.Event
import scala.concurrent.duration.Duration

class RiskShieldEngine extends FraudEngine {
	override def initialize(params: Map[String, Any]) = {

	}

	override def setTimeOut(t: Duration) = {

	}

	override def ask(e: Event): FraudResult = {
		FraudResult(-1)
	}
}