package com.ing.rawsocket

import java.io.{PrintStream, OutputStreamWriter, InputStreamReader, BufferedReader}
import java.net.{InetAddress, Socket}

import scala.io.BufferedSource

object Client {
	def main(args: Array[String]) {
		val s = new Socket(InetAddress.getByName("localhost"), 1324)
		val out = new PrintStream(s.getOutputStream())
		out.println("Hello, world!")
		out.flush()
		s.close()
	}
}