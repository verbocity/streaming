package com.ing.journal.spark

import akka.actor.Actor
import com.ing.data.Transaction
import com.ing.data.Event
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD

class GroupActor() extends Actor {
	override def receive = {
		case StreamAction(rdd: RDD[Event]) =>
		case EventAction(transaction) =>
		case _ =>
	}
}