package com.ing.streaming.actors

import akka.actor.Actor
import com.ing.streaming.actors.traits.{EventAction, StreamAction}
import com.ing.streaming.data.{JsonResult, Event}
import com.ing.streaming.spark.HttpSend
import org.apache.spark.rdd.RDD
import com.ing.streaming.utils.Utils
import org.apache.spark.SparkContext._

class RegionActor extends Actor with HttpSend {
	lazy val cityToMunicipalityMap = Utils.getCityMunicipalityMapping()
	lazy val municipalityToCodeMap = Utils.getNLMapConversion()

	override def receive = {
		case StreamAction(rdd: RDD[Event]) =>
			calculateGroups(rdd)
			calculateRegionCounts(rdd)
		case EventAction(transaction) =>
		case _ =>
	}

	def calculateGroups(rdd: RDD[Event]) = {
		val grouped: Map[String, Int] = rdd
			.filter(e => e.getField("status").toInt != 0)
			.groupBy(e => e.getField("status"))
			.map(group => (group._1.toString, group._2.size))
			.collect()
			.toMap

		send("line", JsonResult.mapToJson(grouped))
	}

	def calculateRegionCounts(rdd: RDD[Event]) = {
		val nl: Map[String, Int] = getCounts(rdd, "city",
			municipalityToCodeMap,
			cityToMunicipalityMap)
		send("NL", JsonResult.mapToCountryJson(nl))

		val europe = Utils.createEuropeCountryCounts().toMap
		send("europe", JsonResult.mapToCountryJson(europe))

		val world = getCounts(rdd, "country")
		send("world", JsonResult.mapToCountryJson(world))
	}

	def getCounts(rdd: RDD[Event], field: String): Map[String, Int] = {
		rdd.map(e => (e.getField(field), 1))
			.reduceByKey(_ + _)
			.collect()
			.toMap
	}

	def getCounts(rdd: RDD[Event], field: String,
	              municipalityToCodeMap: Map[String, String],
	              cityToMunicipalityMap: Map[String, String]): Map[String, Int] = {
		rdd.map(e => {
			// We have a city
			val value = e.getField(field) match {
				case "s-gravenhage" =>
					"'s-gravenhage"
				case "s-hertogenbosch" =>
					"'s-hertogenbosch"
				case "valkenburg lb" =>
					"valkenburg"
				case "amsterdam zui" =>
					"amsterdam"
				case "hengelo ov" =>
					"hengelo"
				case _ =>
					e.getField(field)
			}

			val code = municipalityToCodeMap.getOrElse(value, "")

			if (code != "") {
				(code, 1)
			} else {
				// Look up mapping from city name to municipality
				var municipality: String = null
				municipality = cityToMunicipalityMap.getOrElse(value, "")

				var code: String = null
				if (municipality != "") {
					code = municipalityToCodeMap.getOrElse(municipality, "")

					if (code != null) {
						(code, 1)
					} else {
						("", 1)
					}
				} else {
					("", 1)
				}
			}
		}).reduceByKey(_ + _).collect().toMap
	}
}