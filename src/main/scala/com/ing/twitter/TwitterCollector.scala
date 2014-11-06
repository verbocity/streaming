package com.ing.twitter

import com.ing.utils.Utils
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.{Seconds, StreamingContext}

object TwitterCollector {
	def main(args: Array[String]) {
		Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
        Logger.getLogger("org.apache.spark.storage.BlockManager").setLevel(Level.ERROR)

		Utils.configureTwitterCredentials()
		val ssc = new StreamingContext("local[12]", "Twitter Downloader", Seconds(60))
		val tweets = TwitterUtils.createStream(ssc, None, Seq())

		tweets.foreachRDD(rdd => println("-----\nNumber of tweets: " + rdd.count()))
		val hashtags = tweets.flatMap(status => status.getText.split(" ").filter(_.startsWith("#")))

		val top = hashtags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(60))
						  .map { case (topic, count) => (count, topic) }
						  .transform(_.sortByKey(ascending = false))

		top.foreachRDD(rdd => {
			val toplist = rdd.take(10)
			println("Popular topics in last 60 seconds (%s total):".format(rdd.count()))
			toplist.foreach{case (count, tag) => println("%s (%s tweets)".format(tag, count))}
		})

		ssc.checkpoint("/tmp")
		ssc.start()
		ssc.awaitTermination()
	}
}