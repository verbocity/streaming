package com.ing.streaming.actors

import akka.actor.{Actor, ActorRef}
import com.ing.streaming.actors.traits.StreamAction
import com.ing.streaming.data.Event
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Duration
import scala.collection.mutable.Map

case class SetActors(actors: List[ActorRef])

class AggregationActor(senderActor: ActorRef, levels: List[Duration], baseWindow: Duration)
	extends Actor {
	var actors = List.empty[ActorRef]
	val map = initializeMap(levels)

	override def receive = {
		// Receives an RDD every baseWindow, which is 1 second
		case StreamAction(rdd: RDD[Event]) =>
			/**
			 * Aggregate the message for all levels.
			 * levels:
			 * x second
			 * y = (x * n1) seconds
			 * z = (y * n2) seconds
			 *
			 * for instance:
			 * 1 second
			 * 5 seconds, n1 = 5
			 * 60 seconds, n2 = 12
			 *
			 * every tick, add 1 second window to 5 second window
			 * every 5 ticks, reset 5 second window
			 * add 5 second window to 60 second window
			 *
			 * every tick, add 5 second window to 60 second window
			 * every 60 / 5 = 12 ticks, reset 60 second window
			 */
			/*
			for (duration <- map.keys.toList.sortBy(_.milliseconds)) {
				if (map(duration)._2 > (duration.milliseconds / 1000.0).toInt) {
					map.put(duration, (null, 0))
				} else {
					val entry = map.get(duration)

					entry match {
						case Some(value) =>
							val (r, count) = map.get(duration).get
							val combined = if (r != null) rdd ++ r else rdd
							map.put(duration, (combined, count + 1))
						case _ =>
							println("Error")
					}
				}
			}
			*/
		case SetActors(list) =>
			this.actors = list
		case _ =>
	}

	def initializeMap(levels: List[Duration]): Map[Duration, (RDD[Event], Int)] = {
		val result = Map.empty[Duration, (RDD[Event], Int)]
		levels.foreach((d: Duration) => {
			result.put(d, (null, 0))
		})

		result
	}
}