package com.ing.streaming.data

import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class Transaction(id: Long,
                  time: Long,
                  trantype: Int,
                  out: Boolean,
                  acc1: BigInt,
                  acc2: BigInt,
                  amount: Float,
                  descr: String)

object JsonImplicits extends DefaultJsonProtocol with SprayJsonSupport {
	implicit val transactionFormat = jsonFormat8(Transaction)
}