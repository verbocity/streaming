package com.ing.streaming.eval

import java.io.{File, FileWriter}
import com.ing.streaming.data.Event
import com.ing.streaming.fraud.FraudResult

import scala.io.Source
import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import com.twitter.io.TempFile

@RunWith(classOf[JUnitRunner])
class EvalTest extends WordSpec {
	/**
	 * Choices:
	 * 1) Python
	 *    + flexible and powerful
	 *    + runs on JVM
	 *    - slower than Scala code
	 *    - hard to learn
	 * 2) R
	 *    + flexible and powerful
	 *    + runs on JVM
	 *    - slower than Scala code
	 *    - hard to learn
	 * 3) SQL
	 *    + useful paradigm for selecting data
	 *    - not yet integrated into this application
	 * 4) Scala
	 *    + flexible and powerful
	 *    + extremely fast
	 *    + runs on JVM
	 *    - hard to learn
	 * 5) DSL
	 *    + completely customizable
	 *    + tailorable to requirements
	 *    - needs to be built separately
	 *    - needs to be maintained and extended
	 * 6) XPath
	 *    + flexible for data access
	 *    - not a full alternative to other languages
	 * 7) JSON
	 *    + can be more easily understood than other languages
	 *    + flexible
	 *    - becomes one big mess
	 *    - need to define a lot of atomic operations
	 */
	"Evaluator" should {
		"Event in, FraudResult out" in {
			// TODO: Wbat code to include? What is the environment in which to compile the rule?
			// TODO: Only include FraudResult and Event, or include something else?
			// TODO: Why is TempFile.fromResourcePath not finding files?
			// Find path of currently running class and assume this is the base
			// directory for all the objects that are defined in the string
			// base path already included, but still needs fully qualified import statements
			val rule = "(e: Event) => { FraudResult(if (e.time > 10) 25 else -1) }"
			val e = Event(20, 100, "")
			val result: FraudResult = Eval.apply[Event, FraudResult](rule, e)
			assert(result.getParam == 25)
		}
		
		"int in, int out" in {
			val expr = "(x: Int) => { if (x + 2 == 20) 13 else 12 }"
			val x1 = 10
			val result1 = Eval.apply[Int, Int](expr, x1)
			assert(result1 == 12)

			val x2 = 18
			val result2 = Eval.apply[Int, Int](expr, x2)
			assert(result2 == 13)
		}

		"int in, nothing out" in {
			val expr = "(x: Int) => { if (x + 2 == 20) println(\"int in, nothing out\") }"
			val x = 10
			Eval.apply[Int, Unit](expr, x)
		}

		"nothing in, int out" in {
			val expr = "if (10 < 20) 11 else 13"
			val result = Eval.apply[Int](expr)
			assert(result == 11)
		}

		"nothing in, nothing out" in {
			val expr = "println(\"nothing in, nothing out\")"
			Eval.apply(expr)
		}

		/*
		"apply('expression')" in {
			assert(Eval.apply[Int]("1 + 1") == 2)
		}

		"apply(new File(...))" in {
			assert((new Eval).apply[Int](TempFile.fromResourcePath("/OnePlusOne.scala")) == 2)
		}

		"apply(new File(...), new File(...))" in {
			val derived = (new Eval).apply[() => String](
				TempFile.fromResourcePath("/Base.scala"),
				TempFile.fromResourcePath("/Derived.scala"))
			assert(derived() == "hello")
		}

		"apply(new File(...) with a dash in the name with target" in {
			val f = File.createTempFile("eval", "target")
			f.delete()
			f.mkdir()
			val e = new Eval(Some(f))
			val sourceFile = TempFile.fromResourcePath("/file-with-dash.scala")
			val res: String = e(sourceFile)
			assert(res == "hello")
			val className = e.fileToClassName(sourceFile)
			val processedSource = e.sourceForString(Source.fromFile(sourceFile).getLines.mkString("\n"))
			val fullClassName = "Evaluator__%s_%s.class".format(
				className, e.uniqueId(processedSource, None))
			val targetFileName = f.getAbsolutePath() + File.separator + fullClassName
			val targetFile = new File(targetFileName)
			assert(targetFile.exists)
		}

		"apply(new File(...) with target" in {
			val f = File.createTempFile("eval", "target")
			f.delete()
			f.mkdir()
			val e = new Eval(Some(f))
			val sourceFile = TempFile.fromResourcePath("/OnePlusOne.scala")
			val res: Int = e(sourceFile)
			assert(res == 2)

			// make sure it created a class file with the expected name
			val className = e.fileToClassName(sourceFile)
			val processedSource = e.sourceForString(Source.fromFile(sourceFile).getLines.mkString("\n"))
			val fullClassName = "Evaluator__%s_%s.class".format(
				className, e.uniqueId(processedSource, None))
			val targetFileName = f.getAbsolutePath() + File.separator + fullClassName
			val targetFile = new File(targetFileName)
			assert(targetFile.exists)
			val targetMod = targetFile.lastModified

			// eval again, make sure it works
			val res2: Int = e(sourceFile)
			// and make sure it didn't create a new file (1 + checksum)
			assert(f.listFiles.length == 2)
			// and make sure it didn't update the file
			val targetFile2 = new File(targetFileName)
			assert(targetFile2.lastModified == targetMod)

			// touch source, ensure no-recompile (checksum hasn't changed)
			sourceFile.setLastModified(System.currentTimeMillis())
			val res3: Int = e(sourceFile)
			assert(res3 == 2)
			// and make sure it didn't create a different file
			assert(f.listFiles.length == 2)
			// and make sure it updated the file
			val targetFile3 = new File(targetFileName)
			assert(targetFile3.lastModified == targetMod)

			// append a newline, altering checksum, verify recompile
			val writer = new FileWriter(sourceFile)
			writer.write("//a comment\n2\n")
			writer.close
			val res4: Int = e(sourceFile)
			assert(res4 == 2)
			// and make sure it created a new file
			val targetFile4 = new File(targetFileName)
			assert(!targetFile4.exists)
		}

		"apply(InputStream)" in {
			assert((new Eval).apply[Int](getClass.getResourceAsStream("/OnePlusOne.scala")) == 2)
		}

		"uses deprecated" in {
			val deprecated = (new Eval).apply[() => String](
				TempFile.fromResourcePath("/Deprecated.scala"))
			assert(deprecated() == "hello")
		}

		"inPlace('expression')" in {
			// Old object API works
			Eval.compile("object Doubler { def apply(n: Int) = n * 2 }")
			assert(Eval.inPlace[Int]("Doubler(2)") == 4)
			assert(Eval.inPlace[Int]("Doubler(14)") == 28)
			// New class API fails
			// val eval = new Eval
			// eval.compile("object Doubler { def apply(n: Int) = n * 2 }")
			// assert(eval.inPlace[Int]("Doubler(2)") === 4)
			// assert(eval.inPlace[Int]("Doubler(14)") === 28)
		}

		"check" in {
			(new Eval).check("23")
			intercept[Eval.CompilerException] {
				(new Eval).check("invalid")
			}
		}

		"#include" in {
			val derived = Eval[() => String](
				TempFile.fromResourcePath("/Base.scala"),
				TempFile.fromResourcePath("/DerivedWithInclude.scala"))
			assert(derived() == "hello")
			assert(derived.toString == "hello, joe")
		}

		"recursive #include" in {
			val derived = Eval[() => String](
				TempFile.fromResourcePath("/Base.scala"),
				TempFile.fromResourcePath("/IncludeInclude.scala"))
			assert(derived() == "hello")
			assert(derived.toString == "hello, joe; hello, joe")
		}

		"toSource returns post-processed code" in {
			val derived = Eval.toSource(TempFile.fromResourcePath("/DerivedWithInclude.scala"))
			assert(derived.contains("hello, joe"))
			assert(derived.contains("new Base"))
		}

		"throws a compilation error when Ruby is #included" in {
			intercept[Throwable] {
				Eval[() => String](
					TempFile.fromResourcePath("RubyInclude.scala")
				)
			}
		}

		"clean class names" in {
			val e = new Eval()
			// regular old scala file
			assert(e.fileToClassName(new File("foo.scala")) == "foo")
			// without an extension
			assert(e.fileToClassName(new File("foo")) == "foo")
			// with lots o dots
			assert(e.fileToClassName(new File("foo.bar.baz")) == "foo$2ebar")
			// with dashes
			assert(e.fileToClassName(new File("foo-bar-baz.scala")) == "foo$2dbar$2dbaz")
			// with crazy things
			assert(e.fileToClassName(new File("foo$! -@@@")) == "foo$24$21$20$2d$40$40$40")
		}
		*/
	}
}