package com.ing.streaming.eval

import javax.script.ScriptEngineManager

object REval {
	def main(args: Array[String]): Unit = {
		val engine = new ScriptEngineManager().getEngineByName("Renjin")

		engine.eval("df <- data.frame(x=1:10, y=(1:10) + rnorm(n=10))")
		engine.eval("print(df)")
		engine.eval("model <- lm(y ~ x, df)")
		engine.eval("print(model)")
		engine.eval("print(summary(model))")
	}
}