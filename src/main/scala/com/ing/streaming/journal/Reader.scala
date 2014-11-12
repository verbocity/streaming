package com.ing.streaming.journal

import java.io.{BufferedReader, FileReader}
import java.util.Random
import akka.actor.{ActorRef, Actor}
import org.uncommons.maths.random.ContinuousUniformGenerator
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

case class ReadNextLogEntry()

/**
 * Overall design:
 *
 * log -> Reader -> Parser -> Queue <-> Clock -> Sender
 *
 * Reads files from the log and passes a single text representation
 * of an event to the parser.
 * @param filename The filename of the log file to parse
 * @param density The density of the read events per
 *                unit of time. For instance, 0.5 = half
 *                of the events per second are actually
 *                passed through to the parser.
 *                1.0 = all events.
 *                0 < density <= 1
 * @param maxRows The maximum number of events that
 *                must be read from the file
 */
class Reader(filename: String, density: Float, maxRows: Int, parser: ActorRef, sep: Either[String, Int]) extends Actor {
	val gen = new ContinuousUniformGenerator(0, 1, new Random())
	var reader = new BufferedReader(new FileReader(filename))
	var processed: Int = 0
	var offset = 0
	var paused = false
	var clock: ActorRef = _

	def receive = {
		case ReadNextLogEntry() =>
			if (!paused) {
				if ((maxRows == 0) || (maxRows > 0 && processed <= maxRows)) {
					val entry = getNextEntry()

					if (entry == null) {
						// We are done with reading
						println("Reader: log exhausted. Read " + processed + " items.")
						paused = true
					} else {
						// Pass message probabilistically
						// This results in desired message density
						if (gen.nextValue < density) {
							parser ! ParseLogEntry(entry)
						}

						processed += 1
					}
				}

				if (processed == maxRows) {
					println("Processed the maximum number of " + maxRows + " rows.")
					// clock ! Stop()
				}

				self ! ReadNextLogEntry()
			}
		case QueueFull() =>
			println("Pausing reader")
			paused = true
		case Continue() =>
			println("Continueing reader")
			paused = false
			self ! ReadNextLogEntry()
		case _ =>
			println("Reader: Error")
	}

	private def getNextEntry(): String = {
		if (sep.isLeft) {
			// String separator found
			val separator: String = sep.left.get
			var untilNow = ListBuffer.empty[String]

			breakable {
				while (true) {
					val line: String = reader.readLine()

					if (line == null) {
						return null
					}

					if (separator == "\n") {
						return line
					} else if (line.contains(separator)) {
						val index = line.indexOf(separator)
						val substring = line.substring(0, index)
						untilNow += substring.trim()
						break
					} else {
						untilNow += line
					}
				}
			}

			untilNow.mkString(separator)
		} else {
			// Fixed byte length found
			val length: Int = sep.right.get
			val buf: Array[Char] = new Array[Char](length)
			reader.read(buf, offset, length)
			val result = new String(buf)
			offset += result.length

			result.trim
		}
	}

	def setClock(clock: ActorRef) {
		this.clock = clock
	}
}