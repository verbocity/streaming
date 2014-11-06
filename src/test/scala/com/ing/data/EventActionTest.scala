package com.ing.data

import org.joda.time.DateTime
import org.scalatest.FunSuite
import spray.json._

class EventActionTest extends FunSuite {
	test("Create a new transaction") {
		val dt = new DateTime(2014, 10, 11, 13, 28, 15, 253).getMillis
		val trantype = 2
		val out = true
		val acc1 = 12
		val acc2 = 15
		val amount = 100.50f

		val t: Transaction = new Transaction(1, dt, trantype, out, acc1, acc2, amount, "blabla")

		assert(t.id > 0)
		assert(t.time == dt)
	}

	test("Create a new Event") {
		val event1 = new Event(1, 1, "{}")
		val event2 = Event(2, 2, "bla")
	}

	test("Create a new Event with a complex structure") {
		val json = """{ "id": "test1", "datetime": "14334", "inner": { "bla1": "aaa", "bla2": "bbb" }}"""
		val obj = json.parseJson.asJsObject
		val event1: Event = new Event(json)

		assert(event1.getField("id") == "test1")
		assert(event1.getField("inner/bla1") == "aaa")

		// Corner cases
		assert(event1.getField("") == null)
		assert(event1.getField(null) == null)
		assert(event1.getField("/") == null)
		assert(event1.getField("///") == null)
		assert(event1.getField("/blabla/1") == null)
	}
}