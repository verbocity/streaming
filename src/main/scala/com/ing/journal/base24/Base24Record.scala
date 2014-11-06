package com.ing.journal.base24

import com.ing.data.Event

class Base24Record(val iban: String,
                   val description: String,
				   val city: String,
				   val country: String,
				   val datetime: Long,
				   val amount: Float,
				   val status: Int) {
	def toJsonString(): String = {
		"{\"iban\": \"" + iban + "\", " +
		"\"description\": \"" + description + "\", " +
		"\"city\": \"" + city + "\", " +
		"\"country\": \"" + country + "\", " +
		"\"datetime\": \"" + datetime + "\", " +
		"\"amount\": \"" + amount + "\", " +
		"\"status\": \"" + status + "\" }"
	}

	def isValid(): Boolean = {
		(!description.isEmpty
				&& !city.isEmpty
				&& !country.isEmpty
				&& datetime > 0
				&& amount > 0
				&& amount <= 10000
				&& status == 0)
	}

	override def toString(): String = {
		s""""$iban";"${description.replace("\"", "").replace(";", "")}";"$city";"$country";"$datetime";"$amount";"$status""""
	}
}