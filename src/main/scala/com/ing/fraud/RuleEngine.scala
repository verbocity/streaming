package com.ing.fraud

import com.ing.data.Event

import scala.concurrent.duration.Duration

class RuleEngine extends FraudEngine {
	private var rules = List.empty[Rule]

	override def initialize(params: Map[String, Any]) {

	}

	override def ask(e: Event): FraudResult = {
		null
	}

	override def setTimeOut(t: Duration) {

	}
}
