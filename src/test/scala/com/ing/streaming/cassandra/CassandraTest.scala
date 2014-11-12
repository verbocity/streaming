package com.ing.streaming.cassandra

import com.ing.streaming.data.Event
import org.scalatest.FunSuite
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.Success

class CassandraTest extends FunSuite with ScalaFutures {
	test("Insert and fetch JSON objects into cassandra") {
		val id: Long = 12345
		val time: Long = 6789
		val prop1 = "value1"
		val nested = "nestedvalue1"
		val e = Event(id, time, "{ \"id\": \""
			+ id + "\", \"datetime\": \""
			+ time + "\", \"prop1\": \""
			+ prop1 + "\", \"prop2\": { \"nested\": \""
			+ nested + "\" }}")

		val futureInsert = EventTable.insertNew(e)

		futureInsert onComplete {
			case Success(t) =>
				println("Succesfully inserted row")
		}

		val event = EventTable.getEventById(id)

		whenReady(event, timeout(Span(1, Seconds))) { result =>
			assert(result.get.id == id)
			assert(result.get.time == time)
			assert(result.get.getField("prop1") == prop1)
			assert(result.get.getField("prop2/nested") == nested)
		}
	}
}