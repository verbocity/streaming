package com.ing.fraud

import com.ing.data.Event
import scala.concurrent.duration.Duration

// TODO: Demonstrate model training and scoring with
// TODO: k-means outlier or any other outlier method on transactions
class ModelEngine extends FraudEngine {
	override def initialize(params: Map[String, Any]) {

	}

	override def ask(e: Event): FraudResult = {
		//model.update(e)
		//FraudResult(model.score(e))
		null
	}

	override def setTimeOut(t: Duration) {

	}
}