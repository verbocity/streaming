package com.ing.journal.base24

import java.util.Scanner
import java.io.{PrintWriter, File}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object Base24Converter {
	def main(args: Array[String]) {
		// convertBase24FromASCToCSV(
		//    "C:/Users/Steven/Documents/PX141013.asc",
		//    "C:/Users/Steven/Documents/PX141013.csv")

		convertBase24FromCSVToJson(
			"C:/Users/Steven/Documents/PX141013.csv",
			"C:/Users/Steven/Documents/PX141013.json")
	}

	def convertBase24FromASCToCSV(input: String, output: String) {
		val scanner = new Scanner(new File(input))
		val writer = new PrintWriter(new File(output))
		var i = 0

		// Skip first header line
		scanner.nextLine()

		while (scanner.hasNextLine()) {
			val line: String = scanner.nextLine()

			if (line.startsWith("001472DR")) {
				/**
				 * Get this information from line:
				 * Status (approved/rejected/...)
				 * Transaction type
				 */
				try {
					val iban = null // SecurityUtils.getIBAN(line.substring(441, 460).trim)
					val description = line.substring(327, 349).trim.toLowerCase.replace("\"", "")
					val city = line.substring(349, 365).trim.toLowerCase.replace("\"", "").replace("'", "")
					val country = line.substring(365, 367).trim
					val datetime = getDateTime(line.substring(826, 841).trim)
					val amount = getAmount(line.substring(463, 482).trim)
					val status = getStatus(line.substring(460, 463))

					val record = new Base24Record(iban, description, city,
													country, datetime, amount, status)

					//if (record.isValid) {
						writer.println(record.toString)
					//}

					if (i % 10000 == 0) {
						println(s"Processed $i records")
					}
				} catch {
					case e: Exception =>
						// Do nothing
				}
			}

			i += 1
		}

		writer.flush()
		writer.close()
	}

	def convertBase24FromCSVToJson(input: String, output: String) {
		val scanner = new Scanner(new File(input))
		val writer = new PrintWriter(new File(output))
		var i = 0

		while (scanner.hasNextLine()) {
			val line: String = scanner.nextLine()
			val cols = line.split(";").map(c => c.replace("\"", "").replace("\\", ""))

			val record = new Base24Record(cols(0), cols(1), cols(2), cols(3),
				cols(4).toLong, cols(5).toFloat, cols(6).toInt)

			writer.println(record.toJsonString())

			if (i % 10000 == 0) {
				println(s"Processed $i records")
			}

			i += 1
		}

		writer.flush()
		writer.close()
	}

	def getStatus(string: String): Int = {
		try	{
			string.toInt
		} catch {
			case e: Exception =>
				println(e)
				0
		}
	}

	val formatter = DateTimeFormat.forPattern("yyMMddHHmmssSSS")
	def getDateTime(string: String): Long = {
		// Format: 141011235518261
		//         yyMMddHHmmssSSS
		val dt: DateTime = formatter.parseDateTime(string)
		dt.getMillis
	}

	def getAmount(string: String): Float = {
		// Format: 00007579
		// this is 75.79 Euro

		try {
			val cents = string.substring(string.length - 2, string.length).toInt
			val euros = string.substring(0, string.length - 2).toInt

			euros + (cents / 100.0f)
		} catch {
			case _: Exception =>
				println(string)
				0.0f
		}
	}
}