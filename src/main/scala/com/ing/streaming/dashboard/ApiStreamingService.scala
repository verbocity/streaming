package com.ing.streaming.dashboard

import akka.actor._
import spray.routing.{RequestContext, HttpService}
import spray.can.Http
import spray.http._
import akka.event.{ActorEventBus, LookupClassification}

case class MessageEvent(channel: String, message: Any)
case class Message(text: String)
case object End

class LookupEventBus extends ActorEventBus with LookupClassification {
	type Event = MessageEvent
	type Classifier = String
	protected def mapSize(): Int = 100

	protected def classify(event: Event): Classifier = {
		event.channel
	}

	protected def publish(event: Event, subscriber: Subscriber): Unit = {
		subscriber ! event.message
	}
}

class ApiPassThroughServiceActor extends Actor with ApiPassThroughService {
	// the HttpService trait defines only one abstract member, which
	// connects the services environment to the enclosing actor or test
	def actorRefFactory = context

	// this actor only runs our route, but you could add
	// other things here, like request stream processing,
	// timeout handling or alternative handler registration
	def receive = runRoute(serviceRoute)
}

trait ApiPassThroughService extends HttpService {
	// we prepend 2048 "empty" bytes to push the browser
	// to immediately start displaying the incoming chunks
	lazy val streamStart = " " * 2048 + "\n\n"
	lazy val streamEnd = "\n\n"
	val eventBus = new LookupEventBus

	val apiIngestRoute = {
		pathPrefix("in" / Segment) { topic =>
			pathEnd {
				post {
					entity(as[String]) { s =>
						eventBus.publish(MessageEvent("/events/" + topic, Message(s)))
						complete(StatusCodes.Created, s)
					}
				}
			}
		}
	}

	val apiStreamRoute = {
		pathPrefix("stream" / Segment) { topic =>
			get {
				ctx => sendStreamingResponse(ctx, topic)
			}
		}
	}

	val renderRoute = {
		path("world") {
			getFromResource("com/ing/dashboard/world.html")
		} ~ path("europe") {
			getFromResource("com/ing/dashboard/europe.html")
		} ~ path("stats") {
			getFromResource("com/ing/dashboard/stats.html")
		} ~ path("nl") {
			getFromResource("com/ing/dashboard/nl.html")
		} ~ path("line") {
			getFromResource("com/ing/dashboard/line.html")
		} ~ path("dashboard") {
			getFromResource("com/ing/dashboard/render.html")
		} ~ pathPrefix("js") {
			getFromResourceDirectory("com/ing/dashboard")
		} ~ pathPrefix("img") {
			getFromResourceDirectory("com/ing/dashboard")
		}
	}

	val serviceRoute = {
		pathPrefix("api") {
			apiIngestRoute ~
			apiStreamRoute
		} ~ renderRoute
	}

	implicit def executionContext = actorRefFactory.dispatcher

	def sendStreamingResponse(ctx: RequestContext, topic: String): Unit = {
		val listener = actorRefFactory.actorOf {
			Props {
				new Actor with ActorLogging {
					val `text/event-stream` = MediaType.custom("text/event-stream")
					MediaTypes.register(`text/event-stream`)
					// we use the successful sending of a chunk as trigger for scheduling the next chunk
					val responseStart = HttpResponse(entity = HttpEntity(`text/event-stream`, streamStart))
					ctx.responder ! ChunkedResponseStart(responseStart).withAck()

					def receive = {
						case Message(s) =>
							val nextChunk = MessageChunk("data: " + s + "\n\n")
							ctx.responder ! nextChunk.withAck()
						case End =>
							ctx.responder ! MessageChunk(streamEnd)
							ctx.responder ! ChunkedMessageEnd
							context.stop(self)
						case ev: Http.ConnectionClosed =>
							log.warning("Stopping response streaming due to {}", ev)
							//context.stop(self)
						case _ =>
					}
				}
			}
		}

		eventBus.subscribe(listener, "/events/" + topic)
	}
}