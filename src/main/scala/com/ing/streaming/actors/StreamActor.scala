package com.ing.streaming.actors

import akka.actor.Actor
import com.ing.streaming.actors.traits.Performance
import com.ing.streaming.actors.traits.{Logging, Metrics, Performance}
import com.ing.streaming.spark.HttpSend

abstract class StreamActor extends Actor
	with Performance
	with Metrics
	with Logging
	with HttpSend {

}