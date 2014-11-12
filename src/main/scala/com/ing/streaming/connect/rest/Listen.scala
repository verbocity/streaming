package com.ing.streaming.connect.rest

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import com.ing.streaming.data.Transaction
import spray.can.Http
import spray.http.{StatusCodes, HttpResponse}
import scala.language.postfixOps
import akka.actor.Actor
import spray.routing.HttpService

object Listen extends App {
	implicit val system = ActorSystem("blablabla")
	val handler = system.actorOf(Props[ServiceActor], "blabla")
	IO(Http) ! Http.Bind(handler, interface = "localhost", port = 8080)
}

class ServiceActor extends Actor with HttpService {
	var nr: Long = 0

	def actorRefFactory = context
	def receive = runRoute {
		pathPrefix("transaction") {
			pathEndOrSingleSlash {
				post {
					entity(as[String]) { t =>
						nr += 1
						if (nr % 1000 == 0) {
							println(s"Received $nr transactions")
						}

						complete {
							HttpResponse(StatusCodes.OK)
						}
					}
				}
			}
		}
	}
}