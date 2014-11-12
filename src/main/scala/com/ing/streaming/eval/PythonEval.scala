package com.ing.streaming.eval

object PythonEval {
	/*
	def main(args: Array[String]) {
		method1()
		method2()
		method3()
	}

	def method1(): Unit = {
		import javax.script.ScriptEngineManager

		val engine = new ScriptEngineManager().getEngineByName("python")

		engine.eval("import sys")
		engine.eval("print sys")
		engine.put("a", 42)
		engine.eval("print a")
		engine.eval("x = 2 + 2")

		val x = engine.get("x")
		System.out.println("x: " + x)
	}

	def method2(): Unit = {
		import org.python.core.PyInteger
		import org.python.core.PyObject
		import org.python.util.PythonInterpreter

		val interp = new PythonInterpreter()

		interp.exec("import sys")
		interp.exec("print sys")
		interp.set("a", new PyInteger(42))
		interp.exec("print a")
		interp.exec("x = 2 + 2")
		val x: PyObject = interp.get("x")
		println("x: " + x)
	}

	def method3() = {
		// To run a pre-compiled script (goes way faster):
		import org.python.util.PythonInterpreter
		import org.python.core.PyCode

		val interp = new PythonInterpreter()

		val script = "import sys\n" +
			"print sys\n" +
			"x = 2 + 2\n" +
			"print x"

		val binary: PyCode = interp.compile(script)
		interp.exec(binary)
	}
	*/
}