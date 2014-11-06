package com.ing.data

import spray.json._

case class Event(id: Long, time: Long, json: String) {
	private lazy val jsObject = json.parseJson.asJsObject

	def this(json: String) {
		this(Event.getID(), Event.getTime(json), json)
	}

	def this(time: Long, json: String) = {
		this(Event.getID(), time, json)
	}

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
	def apply(json: String) = new Event(json)
	def apply(time: Long, json: String) = new Event(Event.getID(), time, json)

	private var previousID = 1

	def getID(): Long = {
		synchronized {
			previousID += 1
			previousID
		}
	}

	def getTime(json: String): Long = {
		json.parseJson
			.asJsObject.fields("datetime")
			.compactPrint.replace("\"", "").toLong
	}
}