package com.ing.data

import org.joda.time.DateTime
import spray.json._

case class Event(id: Long, time: Long, json: String) {
	private lazy val jsObject = json.parseJson.asJsObject

	def this(json: String) {
		this(Event.getOrGenerateID(json),
			 Event.getOrGenerateTime(json),
			 json)
	}

	def this(time: Long, json: String) = {
		this(Event.getOrGenerateID(json), time, json)
	}

	def getJsObject = jsObject

	def getField(name: String): String = {
		getField(name, jsObject)
	}

	def getField(name: String, obj: JsObject): String = {
		var n = name
		var rest: String = ""

		if (name == null) return null

		if (name.contains("/")) {
			if (name.length == 1) return null

			val firstIdx = name.indexOf("/")
			n = name.substring(0, firstIdx)
			rest = name.substring(firstIdx + 1, name.length)
		}

		obj.getFields(n) match {
			case Seq(JsNumber(v: BigDecimal)) =>
				v.toString()
			case Seq(JsString(v: String)) =>
				v
			case Seq(JsObject(v: Map[String, JsValue])) =>
				getField(rest, obj.getFields(n)(0).asJsObject)
			case _ =>
				null
		}
	}
}

object Event {
	def apply(json: String) = {
		new Event(json)
	}

	def apply(time: Long, json: String) = {
		new Event(Event.getOrGenerateID(json), time, json)
	}

	private var previousID = 0

	def getOrGenerateID(json: String): Long = {
		synchronized {
			val field = json.parseJson
				.asJsObject.getFields("id")

			if (field.size > 0) {
				field(0).compactPrint.replace("\"", "").toLong
			} else {
				previousID += 1
				previousID
			}
		}
	}

	def getOrGenerateTime(json: String): Long = {
		val field = json.parseJson
			.asJsObject.fields("datetime")

		if (field != null) {
			field.compactPrint.replace("\"", "").toLong
		} else {
			DateTime.now.getMillis
		}
	}
}