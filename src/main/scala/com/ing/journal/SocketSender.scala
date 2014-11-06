package com.ing.journal

import java.io._
import java.net.{InetAddress, Socket}
import com.ing.data.Event
import akka.actor.{Actor, ActorSystem}

case class Send(e: Event)

/**
 * Sends a transaction to an URL
 * @param host The name of the host
 * @param port The port number
 */
class SocketSender(host: String, port: Int) extends Actor {
	implicit val system = ActorSystem()
	var socket: Socket = null
	var initialized = false
	var out: PrintStream = null
	var latestTime: Long = 0
	var sent: Long = 0

	def receive = {
		case Send(event) =>
			try {
				if (!host.isEmpty && port != 0) {
					if (!initialized) {
						println("Initializing sender")
						socket = new Socket(InetAddress.getByName(host), port)
						out = new PrintStream(socket.getOutputStream())
						initialized = true
					}

					out.println(event.json)
					out.flush()

					sent += 1
				}
			} catch {
				case e: java.net.ConnectException =>
					println("Connection not yet established. Retrying...")
			}
		case _ =>
			println("Error")
	}
}