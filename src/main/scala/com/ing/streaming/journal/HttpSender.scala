package com.ing.streaming.journal

import akka.actor.{ActorSystem, Actor}
import spray.client.pipelining._
import spray.http.{HttpResponse, HttpRequest}
import scala.util.{Failure, Success}
import scala.concurrent.Future

class HttpSender(url: String) extends Actor {
	implicit val system = ActorSystem()
	import system.dispatcher
	val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

	def receive = {
		case Send(event) =>
			val response = pipeline(Post(url, event.json))

			response onComplete {
				case Success(resp) =>
					println("Sent succesfully")
				case Failure(error) =>
					println("Failure: " + error)
			}
	}
}