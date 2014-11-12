package com.ing.streaming.utils

import com.typesafe.config.ConfigFactory

import scala.util.Try

class Configuration {
	val config = ConfigFactory.load()
	lazy val servicePort = Try(config.getInt("service.port")).getOrElse(8080)
	lazy val dbUser = Try(config.getString("db.user")).toOption.orNull
}