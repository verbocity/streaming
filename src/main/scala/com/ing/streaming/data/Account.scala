package com.ing.streaming.data

import org.joda.time.DateTime

/**
 * An account exists of the following information:
 *   acc_id the account ID
 *   first_name the first name of the person that owns the account
 *   last_name the last name of the person that owns the account
 *   date_created the date that the account was created
 *   balance the money in the account
 */
case class Account(acc_id: BigInt,
				   first_name: String,
				   last_name: String,
				   date_created: DateTime,
				   balance: Float)