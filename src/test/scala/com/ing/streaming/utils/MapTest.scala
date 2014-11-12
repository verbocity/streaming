package com.ing.streaming.utils

import org.scalatest.FunSuite
import scala.collection.mutable.{Map => mMap}

class MapTest extends FunSuite {
	test("Read text from file and put into map") {
		val mmap = mMap.empty[String, String]
		val file = scala.io.Source.fromFile("citymunicipality.txt")
		assert(file != null)
		val lines = file.getLines().toArray[String]
		assert(lines.length == 4573)
		val splitted = lines.map(c => c.split(";"))
		assert(splitted.length == 4573)
		val columns = splitted.map(a => (a(0), a(1)))
		assert(columns.length == 4573)
		columns.foreach(p => mmap.put(p._1, p._2))
		assert(mmap.size > 0)
	}
}