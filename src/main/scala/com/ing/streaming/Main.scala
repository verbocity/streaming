package com.ing.streaming

import com.ing.streaming.data.DataGenerator
import org.joda.time.{DateTime, Interval}

object Main {
	def main(args: Array[String]): Unit = {
		val interval = new Interval(DateTime.now(), DateTime.now().minusHours(24))
		val (accounts, transactions) = DataGenerator.generateData(1000, 100000, interval)

		println("ACCOUNTS\n========== ")
		accounts.foreach(account =>
			println("Account ID: " + account.acc_id + "\n" +
				"First name: " + account.first_name + "\n" +
				"Last name: " + account.last_name + "\n" +
				"Date created: " + account.date_created + "\n" +
				"Balance: " + account.balance + "\n"))

		println("TRANSACTIONS\n==========")
		transactions.foreach(trans =>
			println("Transaction ID: " + trans.id + "\n" +
				"Time: " + trans.time + "\n" +
				"Type: " + trans.trantype + "\n" +
				"Outgoing: " + trans.out + "\n" +
				"Account ID 1: " + trans.acc1 + "\n" +
				"Account ID 2: " + trans.acc2 + "\n" +
				"Amount: " + trans.amount + "\n" +
				"Description: " + trans.descr + "\n"))
	}
}