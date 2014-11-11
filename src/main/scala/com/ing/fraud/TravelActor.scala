package com.ing.fraud

import java.io.File
import java.sql.{DriverManager, ResultSet}

import akka.actor.Actor
import com.ing.data.Event
import com.ing.journal.spark.{EventAction, HttpSend, StreamAction}
import org.apache.spark.rdd.RDD

import scala.collection.immutable.{Map => iMap}
import scala.collection.mutable.{Map => mMap}

class TravelActor extends Actor with HttpSend {
	def receive = {
		case EventAction(e: Event) =>
			val iban = e.getField("iban")
			EventHistory.put(iban, e)
			val totalDistance = EventHistory.calculateTotalDistance(iban)

			if (totalDistance > 0) {
				// Print the total list of cities and the time it took to travel there
				send("singletravel", "blabla") // EventHistory.getJsonHistory(iban))
			}
		case StreamAction(rdd: RDD[Event]) =>
			val travelersPerMunicipality =
				rdd.map(e => TravelActor.findJourney(e))
					.filter(_._3 > 0) // must have traveled (distance) more than 0 km
					.map(e => e._2) // _2 = currentCity

			if (travelersPerMunicipality.count() > 0) {
				val counted = travelersPerMunicipality.countByValue()

				val contents = {
					for {
						a <- counted
					} yield s""""${a._1}": "${a._2}", """"
				}.mkString("\n")

				send("travel", "{" + contents + "}")
			}
		case _ =>
	}
}

object TravelActor {
	val distances: iMap[(String, String), Float] = initializeDistances()

	// The number of transactions per municipality where the
	// previous city is not in the same municipality
	def findJourney(e: Event): (String, String, Float, Float) = {
		val iban = e.getField("iban").toString
		val previousEvent = EventHistory.getPreviousEvent(iban)
		val currentCity = e.getField("city").toString

		if (previousEvent != null) {
			val previousTime = previousEvent.getField("datetime").toFloat
			val currentTime = e.getField("datetime").toFloat
			val previousCity = previousEvent.getField("city").toString
			val timeHours = (currentTime - previousTime) / 1000 / 60 / 60

			if (previousCity != currentCity) {
				val distance: Float = distances.getOrElse((previousCity, currentCity), 0)
				(previousCity, currentCity, distance, timeHours)
			} else {
				(currentCity, currentCity, 0, timeHours)
			}
		} else {
			(null, currentCity, 0, 0)
		}
	}

	def initializeDistances(): iMap[(String, String), Float] = {
		// Read MySQL table distances
		{
			for {
				line <- scala.io.Source.fromFile(new File("distances.txt")).getLines()
			} yield (line.split(";")(0), line.split(";")(1)) -> line.split(";")(2).toFloat
		}.toMap
	}
}

object EventHistory {
	private val previousEventsSameAccount = mMap.empty[String, List[Event]]
	private var distances: Map[(String, String), Float] = _

	def getPreviousEvent(iban: String): Event = {
		val list = previousEventsSameAccount.getOrElse(iban, null)

		if (list == null || list.size == 0) {
			null
		} else {
			list(0)
		}
	}

	def put(iban: String, event: Event) = {
		val list = previousEventsSameAccount.get(iban)

		if (list.isDefined) {
			previousEventsSameAccount.put(iban, event :: list.get)
		} else {
			previousEventsSameAccount.put(iban, List(event))
		}
	}

	def calculateTotalDistance(iban: String): Float = {
		val list = previousEventsSameAccount.get(iban)

		if (list.isDefined && list.get.size > 1) {
			list.get
				.sliding(2) // Get pairs of events for each two subsequent list elements
				.map(l => {
					val city1 = cleanUp(l(0).getField("city"))
					val city2 = cleanUp(l(1).getField("city"))
					(city1, city2)
				}) // extract city names
				.map(l => calculateDistance(l._1, l._2)) // Calculate distance between these two cities
				.reduce(_ + _)  // Sum all distances in list
		} else {
			0
		}
	}

	def cleanUp(city: String): String = {
		DistanceCalculator.replaceMap.getOrElse(city, city)
	}

	def calculateDistance(city1: String, city2: String): Float = {
		val city1lower = city1.toLowerCase()
		val city2lower = city2.toLowerCase()

		if (city1lower == city2lower) {
			0
		} else {
			var result: Float = distances.getOrElse((city1lower, city2lower), 0)

			if (result == 0) {
				// Try the inversed tuple
				result = distances.getOrElse((city2lower, city1lower), 0)
			}

			result
		}
	}

	def getHistory(iban: String): List[Event] = {
		previousEventsSameAccount.getOrElse(iban, List())
	}

	def getJsonHistory(iban: String): String = {
		/*for (e <- getHistory(iban)) {
			val city = e.getField("city")
		}
		*/
		null
	}

	def initialize() {
		distances = {
			val result = mMap.empty[(String, String), Float]
			val connStr = "jdbc:mysql://localhost:3306/streaming?user=dbuser"
			classOf[com.mysql.jdbc.Driver]
			val conn = DriverManager.getConnection(connStr)

			try {
				val stmt = conn.createStatement(
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY)

				println("Selecting distances from database...")

				val rs = stmt.executeQuery("select * from distances")

				while (rs.next()) {
					val city1 = rs.getString("city1").toLowerCase()
					val city2 = rs.getString("city2").toLowerCase()
					val distance = rs.getFloat("distance")

					result.put((city1, city2), distance)
				}

				println("Returning result from database")

				result.toMap
			} catch {
				case _: Exception =>
					println("database exception")
					null
			} finally {
				conn.close()
				result
			}
		}
	}
}