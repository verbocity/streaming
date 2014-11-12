package com.ing.streaming.fraud

import com.ing.streaming.data.Event
import scala.concurrent.duration.Duration

abstract class FraudEngine {
	def initialize(params: Map[String, Any])
	def setTimeOut(t: Duration)
	def ask(e: Event): FraudResult
}