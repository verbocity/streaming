package com.ing.journal.spark.analysis

import org.apache.log4j.{Level, Logger}
import org.apache.spark._
import spray.json._
import scala.collection.mutable.{Map => mMap}

object Analysis extends App {
	Logger.getRootLogger().setLevel(Level.OFF)
	val conf = new SparkConf().setMaster("local[4]").setAppName("Analysis")
	val sc = new SparkContext(conf)
	val log = sc.textFile("C:/Users/Steven/IdeaProjects/streaming/PX141013.json")
	val objects = log.map(l => l.replace("\\", "").parseJson.asJsObject)
	val perAccount = objects.groupBy(_.getFields("iban")(0).toString)
	val sorted = perAccount.map(a => (a._1, a._2.size)).sortBy(_._2, ascending = false)

	println("Summary statistics: ")
	val total: Float = objects.map(a => a.getFields("amount")(0).toString.replace("\"", "").toFloat).reduce(_ + _)
	println("Total transaction amount this day: " + total)

	println("The top 10 of active accounts: ")
	sorted.take(10).foreach(i => println(i._1 + ": " + i._2))

	println("The top 10 of frequent travelers with cities")
	perAccount
}