package com.ing.streaming.journal

import com.ing.streaming.data.Transaction
import org.joda.time.DateTime
import org.scalatest.BeforeAndAfterAll
import org.scalatest.MustMatchers
import org.scalatest.WordSpecLike
import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import scala.concurrent.duration._

class ParserTest extends TestKit(ActorSystem("parserSystem"))
	with WordSpecLike
	with MustMatchers
	with BeforeAndAfterAll {

	"A Parser actor" should {
		val senderRef = TestActorRef(new SocketSender("", 0))
		val queueRef = TestActorRef(new Queue(100, senderRef))
		val parserRef = TestActorRef(new Parser(queueRef))
		val queue = queueRef.underlyingActor
		val parser = parserRef.underlyingActor

		"Do nothing when passed an invalid string representation" in {
			parserRef ! ParseLogEntry("blablabla")
			expectNoMsg(100 millis)
		}

		"Enqueue a new Transaction in the queue when given a valid string" in {
			val t = "Transaction(1, 2014-10-10 20:36:35.424, 1, false, 11, 22, 100.4, transaction1)"
			parserRef ! ParseLogEntry(t)
			assert(queue.queue.size == 1)
		}

		"Respond with enqueueing a new transaction in less than 10 ms" in {

		}
	}
}