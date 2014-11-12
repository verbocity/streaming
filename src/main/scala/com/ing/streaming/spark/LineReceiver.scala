package com.ing.streaming.spark

import java.net.{Socket, ServerSocket}

import com.ing.streaming.data.Event
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver

import scala.io.BufferedSource
import scala.slick.util.Logging

class LineReceiver(port: Int) extends Receiver[Event](StorageLevel.MEMORY_ONLY) with Logging {
	// Defines the listen function for individual events
	var listeners: Event => Unit = null

	def onStart() {
		// Start the thread that receives data over a connection
		new Thread("Socket Receiver") {
			override def run() {
				receive()
			}
		}.start()
	}

	def onStop() {
		// There is nothing much to do as the thread calling receive()
		// is designed to stop by itself isStopped() returns fals
	}

	private def receive() {
		val socket = new ServerSocket(port)

		try {
			val s = socket.accept()
			val in = new BufferedSource(s.getInputStream()).getLines()

			while (in.hasNext) {
				val string: String = in.next()
				val received: Event = new Event(string)

				if (listeners != null) {
					// Send to event processing listeners
					listeners(received)
				}

				// Process further for Spark Streaming
				store(received)
			}

			restart("Trying to connect again")
		} catch {
			case e: java.net.ConnectException =>
				restart("Error creating service")
			case t: Throwable =>
				restart("Error receiving data", t)
		} finally {
			if (socket != null) {
				socket.close()
			}
		}
	}

	/**
	 * Sets the listen function for individual events
	 * @param f The function to apply when an event comes in
	 */
	def setListenFunction(f: (Event => Unit)) {
		this.listeners = f
	}
}