package com.ing.fraud

import com.ing.data.Event
import scala.concurrent.duration.Duration

abstract class FraudEngine {
	def initialize(params: Map[String, Any])
	def setTimeOut(t: Duration)
	def ask(e: Event): FraudResult
}