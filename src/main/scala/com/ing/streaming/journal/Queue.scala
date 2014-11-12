package com.ing.streaming.journal

import akka.actor.{ActorRef, Actor}
import com.ing.streaming.data.Event
import org.joda.time.DateTime

case class Enqueue(e: Event)
case class Dequeue(after: Long, maxPerSec: Int)
case class NotifyClock(time: Long)
case class QueueFull()
case class Continue()

/**
 * Represents a queue that queues events.
 */
class Queue(size: Int, urlSender: ActorRef) extends Actor {
	val queue = new scala.collection.mutable.Queue[Event]()
	var clockRunning = false
	var clock: ActorRef = _
	var parser: ActorRef = _
	var countThisSecond: Int = 0
	var currentSecond: Long = 0

	def receive = {
		case Enqueue(event) =>
			if (queue.size == size && size > 0) {
				println("Queue: sending queue full message ("
					+ queue.size + " items in queue)")
				sender ! QueueFull()
			} else {
				queue.enqueue(event)

				if (!clockRunning) {
					println("Queue: Clock is not running. Starting clock.")
					clock ! NotifyClock(event.time)
					clock ! Tick()
					clockRunning = true
				}
			}
		case Dequeue(after, maxPerSec) =>
			val list: Seq[Event] = queue.dequeueAll(e => e.time < after)

			for (e <- list) {
				if ((e.time - currentSecond) > 1000) {
					countThisSecond = 0
					currentSecond = e.time
				}

				if (countThisSecond < maxPerSec || maxPerSec == 0) {
					urlSender ! Send(e)
					countThisSecond += 1
				} else {
					println("Maximum number of events per second exceeded")
				}
			}

			clock ! NotifyClock(after)
		case ClockRunning() =>
			clockRunning = true
		case _ =>
			println("Queue: Error")
	}

	def setClock(clock: ActorRef) {
		this.clock = clock
	}

	def setParser(parser: ActorRef) {
		this.parser = parser
	}
}