package com.ing.streaming.journal

import akka.actor.{ActorRef, Actor}
import com.ing.streaming.journal.base24.Base24Record
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import com.ing.streaming.data.{JsonImplicits, Transaction, Event}
import JsonImplicits._
import spray.json._

case class ParseLogEntry(entry: String)

/**
 * Parses text message in a String and creates an event
 * object from it.
 */
class Parser(queue: ActorRef) extends Actor {
	val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")
	var reader: ActorRef = _

	def receive = {
		case ParseLogEntry(entry) =>
			if (entry != null && !entry.isEmpty) {
				try {
					val e = Event(entry)

					if (e != null) {
						queue ! Enqueue(e)
					} else {
						println("Parser received invalid event")
					}
				} catch {
					case e: Exception =>
						println("Skipping invalid event object")
				}
			}
		case QueueFull() =>
			println("Sending queue full message to reader")
			reader ! QueueFull()
		case Continue() =>
			println("Sending continue message to reader")
			reader ! Continue()
		case _ =>
			println("Parser: Error")
	}

	def setReader(reader: ActorRef) {
		this.reader = reader
	}
}