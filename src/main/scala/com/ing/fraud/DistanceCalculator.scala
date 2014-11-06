package com.ing.fraud

import java.io.FileWriter
import java.nio.file.{Files, Paths}

object DistanceCalculator {
	val replaceMap = Map(
		"amsterdam zui" -> "amsterdam",
		"s gravenhage" -> "'s-gravenhage",
		"s-gravenhage" -> "'s-gravenhage",
		"'s gravenhage" -> "'s-gravenhage",
		"den haag" -> "'s-gravenhage",
		"hardinxveld-g" -> "hardinxveld-giessendam",
		"s hertogenbos" -> "s-hertogenbosch",
		"s-hertogenbo" -> "s-hertogenbosch",
		"hendrik-ido-a" -> "hendrik-ido-ambacht",
		"hoorn nh" -> "hoorn",
		"bergen op zoo" -> "bergen op zoom",
		"schiphol airp" -> "schiphol",
		"luchthaven sc" -> "schiphol",
		"luchth schiph" -> "schiphol",
		"hellevoetslui" -> "hellevoetsluis",
		"breukelen ut" -> "breukelen",
		"rijswijk zh" -> "rijswijk",
		"noordwijk zh" -> "noordwijk",
		"hengelo ov" -> "hengelo",
		"capelle aan d" -> "capelle aan den ijssel",
		"ijsselstein u" -> "ijsselstein utrecht",
		"oosterhout nb" -> "oosterhout",
		"ede gld" -> "ede",
		"nymegen" -> "nijmegen",
		"hoogvliet rot" -> "rotterdam",
		"alphen aan de" -> "alphen aan den rijn",
		"ijsselstein utrecht" -> "ijsselstein"
	)

	def main(args: Array[String]) {
		val filename = "cities.txt"
		val cities: Array[String] = scala.io.Source.fromFile(filename).getLines().toArray[String]
		val filtered = cities.map(c => replaceMap.getOrElse(c, c))
		val counts: Map[String, Int] = filtered.groupBy(c => c).mapValues(_.size)
		val sorted = counts.toArray[(String, Int)].sortWith((i, j) =>  { i._2 > j._2 } ).take(100)
		val list = sorted.map(s => s._1)
		val halfcartesian = for { a <- list; b <- list; if a > b } yield (a, b)
		val commands = halfcartesian.map(pair => "echo \"" + s"${pair._1};${pair._2};" +
			"$" + s"(curl -s http://nl.afstand.org/${pair._1}/${pair._2} | " +
			"grep \"directe route.*<strong>\" | sed 's/<\\/strong>//g' " +
			"| sed 's/^.*>//g' | sed 's/ kilometer.//g')\" >> distances.txt")
		new FileWriter("commands.txt") { write(commands.mkString("\n")); flush(); close() }
	}
}