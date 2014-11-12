package com.ing.streaming.actors

import akka.actor.Actor
import com.ing.streaming.actors.traits.EventAction
import com.ing.streaming.cassandra.EventTable
import scala.concurrent.duration._

class PersistenceActor extends Actor {
	override def receive = {
		case EventAction(e) =>
			EventTable.insertNew(e)
			EventTable.deleteOld(e.time, 5 seconds)
	}
}