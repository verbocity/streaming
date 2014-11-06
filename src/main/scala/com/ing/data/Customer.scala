package com.ing.data

import org.joda.time.DateTime

case class Customer(id: Option[Long],
					firstName: String,
					lastName: String,
					birthday: Option[DateTime])