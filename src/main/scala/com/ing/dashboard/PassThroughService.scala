package com.ing.dashboard

import akka.actor.{ ActorSystem, Props }
import akka.io.IO
import spray.can.Http

object PassThroughService extends App {
	implicit val system = ActorSystem()

	// create and start our service actor
	val service = system.actorOf(Props[ApiPassThroughServiceActor], "api")

	// start a new HTTP server with our service actor as the handler
	IO(Http) ! Http.Bind(service, "localhost", port = 8888)
}