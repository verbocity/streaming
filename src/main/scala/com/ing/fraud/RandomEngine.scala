package com.ing.fraud

import java.util.Random
import com.ing.data.Event
import scala.concurrent.duration.Duration

class RandomEngine extends FraudEngine {
	override def initialize(params: Map[String, Any]) = {}

	override def ask(e: Event): FraudResult = {
		FraudResult(if (new Random().nextBoolean()) -1 else 1)
	}

	override def setTimeOut(t: Duration) = {}
}
