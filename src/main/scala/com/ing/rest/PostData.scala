package com.ing.rest

import com.ing.data.{JsonImplicits, Transaction}
import akka.actor.ActorSystem
import spray.http.HttpRequest
import spray.client.pipelining._
import scala.concurrent.Future
import scala.util.{Success, Failure}
import spray.http._
import org.joda.time.{DateTime => jDateTime}
import JsonImplicits._

object PostData {
	def main(args: Array[String]) {
		implicit val system = ActorSystem()
		import system.dispatcher

		val t = new Transaction(1, jDateTime.now().getMillis, 1, out = false, 10, 11, 100.50f, "transaction1")
		val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

		while (true) {
			val response = pipeline(Post("http://localhost:8080/transaction", t))

			response onComplete {
				case Success(resp) =>
					println(resp.status)
				case Failure(error) =>
					println("Failure: " + error)
			}

			Thread sleep 10
		}
	}
}