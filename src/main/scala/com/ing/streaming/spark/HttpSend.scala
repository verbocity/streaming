package com.ing.streaming.spark

import akka.actor.{Actor, ActorSystem}
import spray.client.pipelining._
import spray.http.{HttpRequest, HttpResponse}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait HttpSend extends Actor {
	val system = ActorSystem()
	import system.dispatcher
	val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

	def send(topic: String, msg: String) = {
		val response = pipeline(Post("http://localhost:8888/api/in/" + topic, msg))

		response onComplete {
			case Success(resp) =>
			case Failure(error) =>
				println("Failure: " + error)
		}
	}
}
