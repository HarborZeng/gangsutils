package cn.tellyouwhat.gangsutils.logger

import cn.tellyouwhat.gangsutils.core.constants.{criticalLog, successLog, traceLog}
import cn.tellyouwhat.gangsutils.core.helper.I18N.getRB
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.PRINTLN_LOGGER
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}

import java.io.ByteArrayOutputStream
import java.util.regex.Pattern
import scala.io.AnsiColor.RESET

class funcsTest extends AnyFlatSpec with Matchers with PrivateMethodTester with BeforeAndAfter {

  before {
    GangLogger.setLoggerAndConfiguration(Map(
      PRINTLN_LOGGER -> LoggerConfiguration(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    ))
    GangLogger()
  }

  after {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
  }

  "timeit" should "time a function invocation and log the start and execution duration" in {
    val stream = new ByteArrayOutputStream()
    Console.withOut(stream) {
      funcs.timeit(1 + 1) shouldBe 2
    }

    stream.toString() should fullyMatch regex
      traceLog.format(s": ${getRB.getString("timeit.start").format(getRB.getString("task"))}") +
        successLog.format(s": ${getRB.getString("timeit.finished").format(getRB.getString("task"), """\d*\.*\d*s""")}")

    stream.reset()
    Console.withOut(stream) {
      a[ArithmeticException] should be thrownBy {
        funcs.timeit(1 / 0)
      }
    }
    stream.toString() should fullyMatch regex
      traceLog.format(s": ${getRB.getString("timeit.start").format(getRB.getString("task"))}") +
        criticalLog.format(s": ${getRB.getString("timeit.failed").format(getRB.getString("task"), """\d*\.*\d*s""")}")
  }


  "printOrLog" should "print to stdout about the built log content or use logger(Logger) to do a log action if the parameter logger is fulfilled" in {
    val stream = new ByteArrayOutputStream()
    Console.withOut(stream) {
      funcs.printOrLog("content", LogLevel.TRACE)
    }
    stream.toString() should fullyMatch regex traceLog.replace(Pattern.quote(RESET), "").format(": content")

    stream.reset()
    Console.withOut(stream) {
      funcs.printOrLog("content", LogLevel.TRACE)(GangLogger(isDTEnabled = false, isHostnameEnabled = false))
    }
    stream.toString() should fullyMatch regex traceLog.format(": content")
  }

}
