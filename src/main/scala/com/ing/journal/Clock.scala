package com.ing.journal

import akka.actor.{ActorRef, Actor, ActorSystem}
import org.joda.time.DateTime
import scala.concurrent.duration._
import com.ing.data.Event

case class Tick()
case class DequeuedTransaction(e: Event)
case class ClockRunning()
case class Stop()

/**
 * Represents the clock that sends data from the queue
 * to a REST interface.
 * @param speed The speed of the clock. 1.0 = real time,
 *              < 1.0 = slowed down, > 1.0 = speed up.
 *              For instance, 1000 = 1000 times as fast.
 *              Cannot be <= 0.
 */
class Clock(speed: Float, maxPerSec: Int, queue: ActorRef) extends Actor {
	// The previous wall clock (real) time of the last Tick message
	var previousRealTime: Long = 0
	// The previous log event (transaction) time that was processed
	var previousEventTime: Long = 0
	// The number of ticks this clock has processed
	var ticksDone: Long = 0
	// The number of ticks processed this (real) second
	var thisSec: Int = 0
	// The previous time of the progress report
	var previousProgress: Long = 0
	// Whether the clock is running or not
	var running = true

	def receive = {
		case Tick() =>
			if (running) {
				// Let the queue know that the clock is running
				queue ! ClockRunning()

				// The actual (real) time at this moment
				val nowTime = DateTime.now()
				val nowTimeMillis = nowTime.getMillis

				// This happens the first time Tick() is called
				if (previousRealTime == 0) {
					previousRealTime = nowTimeMillis
				}

				// The real time that has past since the previous tick
				val realSpan = nowTimeMillis - previousRealTime

				// We must have received at least one transaction time
				if (previousEventTime > 0) {
					// Calculate the amount of time that has passed in the log
					val delta = (realSpan * speed).toInt

					if (delta > 0) {
						val logTime = previousEventTime + delta
						reportProgress(previousEventTime, realSpan, delta, logTime)
						queue ! Dequeue(logTime, maxPerSec)
					}
				} else {
					println("previousEventTime <= 0")
				}

				// Use the latest wall time
				previousRealTime = DateTime.now().getMillis
				ticksDone += 1

				// Send message to initiate the next tick
				val nextMillis = (1000.0 / speed.toFloat).toInt

				implicit val system = ActorSystem()
				import system.dispatcher
				system.scheduler.scheduleOnce(nextMillis millis) {
					self ! Tick()
				}
			} else {
				println("Clock stopped.")
			}
		case Stop() =>
			running = false
			println("Clock received stop message, terminating.")
			System.exit(0)
		case NotifyClock(time) =>
			// Lets the clock know that an event has been processed
			previousEventTime = time
		case _ =>
			println("Clock: Error")
	}

	def reportProgress(previousEventTime: Long, realSpan: Long, delta: Int, logTime: Long) {
		val now = DateTime.now().getMillis()

		if (now - previousProgress > 1000) {
			println("log time: " + new DateTime(logTime).toString() + " (" + logTime + ")")
			previousProgress = now
		}
	}
}