package com.ing.journal

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import akka.testkit._
import com.ing.data.{DataGenerator, Transaction}
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.duration._

class ReaderTest extends TestKit(ActorSystem("ReaderSystem"))
	with DefaultTimeout
	with ImplicitSender
	with WordSpecLike
	with Matchers
	with BeforeAndAfterAll {

	val filename = "output.txt"

	val t1 = new Transaction(1, new DateTime(2014, 1, 1, 0, 0, 0, 0).getMillis,
		1, out = false, 1, 2, 100.50f, "transaction1")
	val t2 = new Transaction(2, new DateTime(2014, 2, 2, 0, 0, 0, 0).getMillis,
		1, out = false, 1, 2, 200.50f, "transaction2")
	val t3 = new Transaction(3, new DateTime(2014, 3, 3, 0, 0, 0, 0).getMillis,
		1, out = false, 1, 2, 300.50f, "transaction3")

	override def afterAll(): Unit = {
		TestKit.shutdownActorSystem(system)
	}

	"A Reader actor" should {
		// DataGenerator.writeToFile(filename, List(t1, t2, t3))

		val senderRef = TestActorRef(new SocketSender("", 0))
		val queueRef = TestActorRef(new Queue(100, senderRef))
		val parserRef = TestActorRef(new Parser(queueRef))
		val readerRef = TestActorRef(new Reader(filename, 1, 0, parserRef, Left("\n")))
		parserRef.underlyingActor.setReader(readerRef)

		val reader = readerRef.underlyingActor
		val queue = queueRef.underlyingActor

		"Read a transaction and pass it to the queue" in {
			assert(reader.offset == 0)
			assert(reader.processed == 0)

			readerRef ! ReadNextLogEntry()

			// Wait until the log is read
			expectNoMsg(300 millis)
		}
	}
}