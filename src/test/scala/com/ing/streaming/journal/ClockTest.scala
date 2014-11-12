package com.ing.streaming.journal

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import com.ing.streaming.data.Transaction
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}
import scala.concurrent.duration._
import com.ing.streaming.data.Event
import spray.json._
import com.ing.streaming.data.JsonImplicits._

class ClockTest extends TestKit(ActorSystem("clockSystem"))
	with WordSpecLike
	with MustMatchers
	with BeforeAndAfterAll {

	override def afterAll {
		TestKit.shutdownActorSystem(system)
	}

	"A Clock actor" should {
		// When replaying 24 hours (24 * 60 * 60 = 86400 sec) in 5 minutes (5 * 60 = 300 sec)
		// a speed of 86400 / 300 = 288 times is needed
		val speed = 288.0f
		val queueSize = 100 // not used right now

		val senderRef = TestActorRef(new SocketSender("", 0))
		val queueRef = TestActorRef(new Queue(queueSize, senderRef))
		val clockRef = TestActorRef(new Clock(speed, 0, queueRef))
		queueRef.underlyingActor.setClock(clockRef)
		val clock = clockRef.underlyingActor

		"process injected messages properly" in {
			assert(clock.previousRealTime == 0)
			assert(clock.previousEventTime == 0)
			assert(clock.ticksDone == 0)

			// Start the clock. From there on, it calls itself
			clockRef ! Tick()

			val logDateStart = new DateTime(2014, 5, 4, 13, 28, 15, 435)

			for (i <- 1 to 5) {
				val t = new Transaction(1, logDateStart.getMillis + (i hours).toMillis,
					1, out = false, 11, 22, 100.40f, "transaction1")
				println("Created transaction " + t)
				queueRef ! Enqueue(new Event(t.toJson.compactPrint))
			}

			// Wait for a period of time
			expectNoMsg(100 millis)

			// Simulate a new transaction coming in
			clockRef ! NotifyClock(logDateStart.getMillis)

			expectNoMsg(600 millis)

			// This number is uncertain due to scheduling
			assert(clock.ticksDone >= 18 &&
				   clock.ticksDone <= 24)
		}
	}
}