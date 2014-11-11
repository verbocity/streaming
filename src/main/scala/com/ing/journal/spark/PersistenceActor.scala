package com.ing.journal.spark

import akka.actor.Actor
import com.ing.cassandra.EventTable
import scala.concurrent.duration._

class PersistenceActor extends Actor {
	override def receive = {
		case EventAction(e) =>
			EventTable.insertNew(e)
			EventTable.deleteOld(e.time, 5 seconds)
	}
}