package com.ing.streaming.connect.rawsocket

import java.io.{PrintStream, InputStreamReader, BufferedReader}
import java.net.{ServerSocket, Socket}

import scala.io.BufferedSource

object Server {
	def main(args: Array[String]) {
		val server = new ServerSocket(1324)

		while (true) {
			val s = server.accept()
			val in = new BufferedSource(s.getInputStream()).getLines()
			println("Server has received: " + in.next())
			s.close()
		}
	}
}