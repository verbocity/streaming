package com.ing.streaming.actors

import akka.actor.Actor
import com.ing.streaming.actors.traits.{EventAction, StreamAction}
import com.ing.streaming.data.Transaction
import com.ing.streaming.data.Event
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD

class GroupActor() extends Actor {
	override def receive = {
		case StreamAction(rdd: RDD[Event]) =>
		case EventAction(transaction) =>
		case _ =>
	}
}