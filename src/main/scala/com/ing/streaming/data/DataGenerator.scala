package com.ing.streaming.data

import java.util
import java.util.Random
import scala.collection.mutable.MutableList
import org.joda.time.DateTime
import org.joda.time.Interval
import org.uncommons.maths.random.{ContinuousUniformGenerator, DiscreteUniformGenerator, GaussianGenerator}
import java.io._

object DataGenerator {
	def generateData(nr_accounts: Int, nr_transactions: Int, interval: Interval): (List[Account], List[Transaction]) = {
		println("Generating transaction data...")

		println("Interval: " + interval)

		val accounts: List[Account] = generate_accounts(nr_accounts)
		val transactions: List[Transaction] = generate_transactions(nr_transactions, accounts, interval)

		(accounts, transactions)
	}

	def createFile(file: String, x: Int, start: DateTime, end: DateTime) {
		val interval = new Interval(start, end)
		val (_, transactions) = generateData(x / 5, x, interval)
		writeToFile(file, transactions)
	}

	def writeToFile(file: String, transactions: List[Transaction]) {
		val writer = new PrintWriter(new File(file))

		var i: Int = 0

		for (t <- transactions) {
			writer.write(t + "\n")

			i += 1

			if (i % 100000 == 0) {
				println(s"Written $i transactions")
			}
		}

		println(s"Finished writing $i transactions.\n")

		writer.close()
	}

	private def generate_accounts(number: Int): List[Account] = {
		val result: MutableList[Account] = MutableList()

		val accountInterval = new Interval(
			new DateTime(2010, 1, 1, 0, 0, 0, 0),
			new DateTime(2014, 1, 1, 0, 0, 0, 0))

		for (i <- Range(0, number)) {
			val acc_id = number + generate_int(0, number)
			val first_name = generate_string(5, 10)
			val last_name = generate_string(5, 30)
			val date_created = generate_timestamp(accountInterval)
			val balance = generate_normal(5000, 1000)

			result += Account(acc_id, first_name, last_name, date_created, balance)
		}

		result.toList
	}

	private def generate_transactions(number: Int, accounts: List[Account], interval: Interval): List[Transaction] = {
		val result: MutableList[Transaction] = MutableList()

		// The total duration of the interval in seconds
		val seconds: Int = interval.toPeriod.toStandardSeconds.getSeconds
		println("Interval size: " + seconds + " seconds.")

		// The number of transactions per second
		val transPerSec: Float = number.toFloat / seconds.toFloat
		println("Creating " + transPerSec + " transactions per second on average")

		for (i <- Range(0, number)) {
			val id = number + generate_int(0, number)
			val time = generate_timestamp(interval)
			val trantype = generate_int(3)
			val out = generate_boolean()
			val acc1 = generate_int(number)
			val acc2 = generate_int(number)
			val amount = generate_normal(5000, 2000)
			val descr = generate_string(10, 20)

			result += new Transaction(id, time.getMillis, trantype, out, acc1, acc2, amount, descr)

			if (i % 100000 == 0) {
				println(s"Generated $i transactions")
			}
		}

		println(s"Finished generating $number transactions.")
		result.sortBy(_.time).toList
	}

	private def generate_int(min: Int, max: Int): Int = {
		val gen = new DiscreteUniformGenerator(min, max, new Random())
		gen.nextValue
	}

	private def generate_int(max: Int): Int = {
		generate_int(0, max)
	}

	private def generate_int(min: Int, max: Int, others: util.HashSet[BigInt]): BigInt = {
		val result = generate_int(min, max)

		if (others != null && others.contains(result)) {
			println("Already exists, trying again")
			// Try again if it already exists
			generate_int(min, max, others)
		}

		BigInt(result)
	}

	private def generate_uniform(min: Int, max: Int): Double = {
		val gen = new ContinuousUniformGenerator(min, max, new Random())
		gen.nextValue
	}

	private def generate_boolean(): Boolean = {
		val gen = new DiscreteUniformGenerator(0, 1, new Random())
		gen.nextValue() == 0
	}

	private def generate_normal(avg: Double, sd: Double): Float = {
		val gen = new GaussianGenerator(avg, sd, new Random())
		gen.nextValue.toFloat
	}

	private def generate_string(minLength: Int, maxLength: Int): String = {
		val avg = (minLength + maxLength) / 2.0
		val sd = avg * 0.5
		val strLength = generate_normal(avg, sd).toInt
		val result = new StringBuilder()

		Range(1, strLength).foreach(
			c => {
				// The range [a-z]
				result += generate_uniform(97, 122).toChar
			}
		)

		result.toString()
	}

	private def generate_timestamp(interval: Interval): DateTime = {
		val secs = interval.toDuration.getStandardSeconds
		val added = generate_int(secs.toInt)
		val millis = generate_int(0, 999)
		val result = new DateTime(interval.getStart.getMillis + (added * 1000) + millis)
		result
	}
}