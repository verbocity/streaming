package com.ing.cassandra

import scala.concurrent. { blocking, Future }
import com.datastax.driver.core.{ Cluster, Session }
import com.websudos.phantom.Implicits._

object DBConnector {
	val keySpace = "streaming"

	lazy val cluster =  Cluster.builder()
		.addContactPoint("localhost")
		.withPort(9042)
		.withoutJMXReporting()
		.withoutMetrics()
		.build()

	lazy val session = blocking {
		cluster.connect(keySpace)
	}
}

trait DBConnector {
	self: CassandraTable[_, _] =>

	def createTable(): Future[Unit] ={
		create.future() map (_ => ())
	}

	implicit lazy val datastax: Session = DBConnector.session
}