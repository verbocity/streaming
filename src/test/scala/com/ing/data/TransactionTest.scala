package com.ing.data

import org.joda.time.DateTime
import org.scalatest.FunSuite
import spray.json._
import JsonImplicits._

class TransactionTest extends FunSuite {
	test("Convert transaction object to JSON string and back") {
		val t = new Transaction(1, DateTime.now().getMillis, 1, out = false, 1, 2, 100.20f, "transaction 1")
		val str = t.toJson
		val converted = str.toJson.convertTo[Transaction]
		assert(t === converted)
	}
}