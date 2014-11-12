package com.ing.streaming.data

/**
 * Represents the result that will be shown on screen
 * every time the interval is refreshed.
 */
object JsonResult {
	// Shows information to be shown on the map
	// of the Netherlands (to be determined)

	object NL {
		var countsPerMunicipality = Map.empty[String, Int]

		def toJson() : String = {
			"{\"Netherlands\": " + mapToCountryJson(countsPerMunicipality) + "}"
		}
	}

	object Europe {
		var countsPerCountry = Map.empty[String, Int]

		def toJson(): String = {
			"{\"Europe\": " + mapToCountryJson(countsPerCountry) + "}"
		}
	}

	object World {
		var countsPerCountry = Map.empty[String, Int]

		def toJson(): String = {
			"{\"World\": " + mapToCountryJson(countsPerCountry) + "}"
		}
	}

	// Shows information for the column chart
	object GroupedTransactions {
		var grouped = Map.empty[String, Int]

		def toJson(): String = {
			"{\"TransactionTypes\": " + mapToJson(grouped) + "}"
		}
	}

	object Statistics {
		var statistics = Map.empty[String, Any]

		def toJson(): String = {
			"{\"Statistics\": " + mapToJson(statistics) + "}"
		}
	}

	def mapToJson(map: Map[String, Any]): String = {
		var result = "{"

		for ((s: String, a: Any) <- map) {
			result += "\"" + s + "\": \"" + a + "\", "
		}

		if (result.length > 1) {
			result.replaceAll(", $", "}")
		} else {
			result + "}"
		}
	}

	def mapToCountryJson(map: Map[String, Any]): String = {
		var result = "["

		for ((s: String, a: Any) <- map) {
			result += "{\"hc-key\": \"" + s + "\", \"value\": \"" + a + "\"},"
		}

		if (result.length > 1) {
			result.replaceAll(",$", "]")
		} else {
			result + "]"
		}
	}
}