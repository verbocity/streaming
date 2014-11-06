package com.ing.journal

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import com.ing.data.{Transaction, DataGenerator}
import org.joda.time.{DateTime, Interval}
import org.scalatest._
import scala.collection.mutable
import scala.concurrent.duration._
import com.ing.data.Event
import com.ing.data.JsonImplicits._
import spray.json._

class QueueTest extends TestKit(ActorSystem("clockQueueSystem"))
	with WordSpecLike
	with MustMatchers
	with BeforeAndAfterAll {

	/**
	 * Generates data as input for our tests
	 * @param total The total number of transactions to generate
	 * @param interval The timespan of the transaction list
	 */
	def generateTransactionList(total: Int, interval: Interval) = {
		// Generate 5 times as less accounts as transactions
		val (_, transactions) = DataGenerator.generateData(total / 5, total, interval)
		assert(transactions.size == total)
	}

	override def afterAll {
		TestKit.shutdownActorSystem(system)
	}

	"A Queue actor" should {
		val senderActorRef = TestActorRef(new SocketSender("", 0))
		val queueActorRef = TestActorRef(new Queue(100, senderActorRef))

		val time = DateTime.now().getMillis
		val t = new Transaction(1, time, 1, out = false, 11, 22, 100.40f, "transaction1")

		/**
		 * Queue works like this:
		 * enqueue -> 3, 2, 1 -> dequeue
		 * 3 was added last, and is last to be dequeued
		 * 1 was added first, and is first to be dequeued
		 */
		"exhibit the following basic queue behavior" in {
			val queue = mutable.Queue.empty[Int]
			queue.enqueue(1)
			queue.enqueue(2)

			val i = queue.dequeue()

			assert(i == 1)
			assert(queue.size == 1)

			queue.enqueue(3)

			assert(queue.head == queue.front)
			assert(queue.last == 3)
		}

		"Do nothing when fetching from an empty queue" in {
			// An arbitrary date in the past
			queueActorRef ! Dequeue(DateTime.now().minusDays(1).getMillis, 0)
			expectNoMsg(10 millis)

			// An arbitrary date in the future
			queueActorRef ! Dequeue(DateTime.now().plusDays(1).getMillis, 0)
			expectNoMsg(10 millis)
		}

		"Enqueue an item to the queue" in {
			queueActorRef ! Enqueue(new Event(t.toJson.compactPrint))
			assert(queueActorRef.underlyingActor.queue.size == 1)
			expectNoMsg(10 millis)

			queueActorRef ! Enqueue(new Event(t.toJson.compactPrint))
			assert(queueActorRef.underlyingActor.queue.size == 2)
			expectNoMsg(10 millis)
		}

		"Dequeue an item from the queue" in {
			queueActorRef ! Dequeue(DateTime.now().getMillis, 0)
			assert(queueActorRef.underlyingActor.queue.size == 0)
			expectNoMsg(10 millis)

			queueActorRef ! Dequeue(DateTime.now().getMillis, 0)
			assert(queueActorRef.underlyingActor.queue.size == 0)
			expectNoMsg(10 millis)
		}

		"Enqueue multiple items to the queue" in {
			val queue = queueActorRef.underlyingActor.queue

			for (i <- 1 to 10) {
				val new_t = new Transaction(t.id, new DateTime(t.time).plusSeconds(i).getMillis,
					t.trantype, t.out, t.acc1, t.acc2, t.amount, t.descr)

				queueActorRef ! Enqueue(new Event(new_t.toJson.compactPrint))
			}

			assert(queue.size == 10)

			val splitTime = new DateTime(t.time).plusSeconds(5).getMillis + 1
			queueActorRef ! Dequeue(splitTime, 0)

			assert(queue.size == 5)
			assert(queue.front.time < queue.last.time)
			// Everything that is still in the queue must be after the split time
			assert(queue.head.time >= splitTime)
			assert(queue.last.time >= splitTime)
		}
	}
}