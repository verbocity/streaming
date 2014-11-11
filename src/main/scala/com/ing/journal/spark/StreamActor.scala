package com.ing.journal.spark

import akka.actor.Actor
import com.ing.journal.spark.traits.{Logging, Metrics, Performance}

abstract class StreamActor extends Actor
	with Performance
	with Metrics
	with Logging
	with HttpSend {

}