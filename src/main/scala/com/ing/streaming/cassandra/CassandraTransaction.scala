package com.ing.streaming.cassandra

import com.ing.streaming.data.Event
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{ Future => ScalaFuture }
import com.datastax.driver.core.{ ResultSet, Row }
import com.websudos.phantom.Implicits._
import scala.concurrent.Future
import scala.util.{Failure, Success}

sealed class EventTable extends CassandraTable[EventTable, Event] {
	// The ID of the JSON object
	object id extends LongColumn(this) with PartitionKey[Long]
	// Extra key column to do fast lookups on (for account number for instance)
	object keycolumn2 extends StringColumn(this) with PartitionKey[String]
	// Extra key column to do fast lookups on
	object keycolumn3 extends StringColumn(this) with PartitionKey[String]
	// The time of the JSON object
	object datetime extends LongColumn(this) with PartitionKey[Long]
	// The string JSON representation
	object string extends StringColumn(this)
	// The map of path/value pairs
	object map extends MapColumn[EventTable, Event, String, String](this)

	 override def fromRow(r: Row): Event = {
		// We only need the JSON string since the
		// other columns are derived from this string
		Event(string(r))
	}
}

object EventTable extends EventTable with DBConnector {
	def insertNew(e: Event): Future[ResultSet] = {
		insert.value(_.id, e.id)
			  .value(_.datetime, e.time)
			  .value(_.string, e.json)
			  .future
	}

	def insertNew(e: Event, keycolumn2: String, keycolumn3: String): Future[ResultSet] = {
		insert.value(_.id, e.id)
			  .value(_.datetime, e.time)
			  .value(_.keycolumn2, keycolumn2)
		      .value(_.keycolumn3, keycolumn3)
		      .future
	}

	def deleteOld(time: Long, duration: Duration) {
		delete.where(_.datetime lt (time - duration.toMillis)).future
	}

	def getEventById(id: Long): Future[Option[Event]] = {
		select.where(_.id eqs id).one
	}

	def getEventsByTime(start: Long, end: Long): Future[Seq[Event]] = {
		select.where(_.datetime gte start).and(_.datetime lte end).fetch()
	}

	def getFieldByIdAndPath(id: Long, path: String): String = {
		val future = select.where(_.id eqs id).one

		future onComplete {
			case Success(option) =>
				if (option.isDefined) {
					return option.get.getField(path)
				}
			case Failure(t) =>
				println("An error has occurred: " + t.getMessage)
		}

		null
	}
}