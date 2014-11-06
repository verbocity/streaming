package com.ing.journal

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import com.ing.data.DataGenerator
import com.ing.journal.spark.JournalListener
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}
import scala.concurrent.duration._

class IntegrationTest extends TestKit(ActorSystem("system"))
	with WordSpecLike
	with MustMatchers
	with BeforeAndAfterAll {

	override def afterAll {
		TestKit.shutdownActorSystem(system)
	}

	"A journal system" should {
		val filename = "C:/Users/Steven/Documents/PX141013.json"
		//createOutputFile(filename)

		val host = "localhost"
		val port = 1357
		val speed = 1000.0f    // Keep equal to or below 288, which is 5 minutes for 24 hours
		val density = 1.0f   // no filtering with density = 1.0f
		val maxRows = 0      // unlimited number of rows
		val maxPerSec = 0    // in simulation time
		val queueSize = 0    // not set

		val senderRef = TestActorRef(new SocketSender(host, port))
		val queueRef = TestActorRef(new Queue(queueSize, senderRef))
		val parserRef = TestActorRef(new Parser(queueRef))
		val readerRef = TestActorRef(new Reader(filename, density, maxRows, parserRef, Left("\n")))
		val clockRef = TestActorRef(new Clock(speed, maxPerSec, queueRef))

		val queue = queueRef.underlyingActor
		val parser = parserRef.underlyingActor
		val reader = readerRef.underlyingActor

		queue.setClock(clockRef)
		parser.setReader(readerRef)
		queue.setParser(parserRef)
		reader.setClock(clockRef)

		"Start ticking of clock" in {
			// Start the Spark Streaming listener
			new Thread { override def run() { JournalListener.main(null) }}.start()
			Thread.sleep(2000)

			// Start the stream passthrough service
			new Thread { override def run() { com.ing.dashboard.PassThroughService.main(null) }}.start()

			// Start up the log processing system
			readerRef ! ReadNextLogEntry()

			expectNoMsg(1000 seconds)
		}
	}

	def createOutputFile(filename: String) {
		val startDate = new DateTime(2014, 5, 4, 0, 0, 0, 0)
		val endDate = new DateTime(2014, 5, 4, 23, 59, 59, 999)
		DataGenerator.createFile(filename, 1000000, startDate, endDate)
	}
}