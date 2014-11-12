package com.ing.streaming.actors

import java.text.{DecimalFormat, NumberFormat}
import java.util.{Currency, Locale}
import org.scalatest.FunSuite

class StatisticsActorTest extends FunSuite {
	test("Test thousand separator") {
		val formatter = NumberFormat.getInstance(Locale.US).asInstanceOf[DecimalFormat]
		formatter.setMaximumFractionDigits(2)
		formatter.setMinimumFractionDigits(2)

		val symbols = formatter.getDecimalFormatSymbols()
		symbols.setGroupingSeparator('.')
		symbols.setDecimalSeparator(',')
		formatter.setDecimalFormatSymbols(symbols)

		val value: Double = 15334322.435
		System.out.println("€ " + formatter.format(value))
	}
}