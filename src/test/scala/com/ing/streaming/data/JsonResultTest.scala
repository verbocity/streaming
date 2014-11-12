package com.ing.streaming.data

import com.ing.streaming.utils.Utils
import org.joda.time.DateTime
import org.scalatest.FunSuite

class JsonResultTest extends FunSuite {
	test("Serializing JsonResult back and forth") {
		JsonResult.Europe.countsPerCountry = Map(
			"NL" -> 10,
			"FR" -> 5,
			"GB" -> 2,
			"ES" -> 12
		)

		JsonResult.World.countsPerCountry
			= Utils.createWorldCountryCounts()

		JsonResult.GroupedTransactions.grouped = Map(
			"0" -> 1434,
			"1" -> 4254,
			"2" -> 5334,
			"3" -> 2443
		)

		JsonResult.Statistics.statistics = Map(
			"currentTime" -> DateTime.now.getMillis(),
			"totalTransactions" -> 144232,
			"totalAmount" -> 4332434.23,
			"averageAmount" -> 25.22,
			"avgTransPerSec" -> 144.2,
			"lastNrTransWindow" -> 112,
			"peakTransPerSec" -> 254,
			"mostFrequentVendor" -> "Albert Heijn",
			"mostFrequentCategory" -> "Retail")

		val result = JsonResult.Europe.toJson()
		val expected = """{"Europe": [{"hc-key": "NL", "value": "10"},{"hc-key": "FR", "value": "5"},{"hc-key": "GB", "value": "2"},{"hc-key": "ES", "value": "12"}]}"""
		assert(result == expected)
	}
}