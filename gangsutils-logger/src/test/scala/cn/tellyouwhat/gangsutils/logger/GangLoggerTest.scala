package cn.tellyouwhat.gangsutils.logger

import cn.tellyouwhat.gangsutils.core.constants.{criticalLog, errorLog, infoLog, successLog, traceLog, warningLog}
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.logger.dest.PrintlnLogger
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}

class GangLoggerTest extends AnyFlatSpec with Matchers with PrivateMethodTester with BeforeAndAfter {

  before {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
  }
  after {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
  }

  behavior of "GangLoggerTest"

  it should "success" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.success("a success log")
    }
    stream.toString should fullyMatch regex successLog.format(": a success log")
  }

  it should "info" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.info("an info log")
    }
    stream.toString should fullyMatch regex infoLog.format(": an info log")
  }

  it should "trace" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.trace("a trace log")
    }
    stream.toString should fullyMatch regex traceLog.format(": a trace log")
  }

  it should "log" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.log("a log", level = LogLevel.TRACE)
    }
    stream.toString() should fullyMatch regex traceLog.format(": a log")
  }

  it should "critical" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.critical("a critical log")
    }
    stream.toString should fullyMatch regex criticalLog.format(": a critical log")
  }

  it should "warning" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.warning("a warning log")
    }
    stream.toString should fullyMatch regex warningLog.format(": a warning log")
  }

  it should "error" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.error("an error log")
    }
    stream.toString should fullyMatch regex errorLog.format(": an error log")
  }

  it should "apply with logPrefix" in {
    val logger2 = GangLogger(isDTEnabled = false, logPrefix = Some("a prefix"), isHostnameEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger2.trace("a log with prefix")
    }
    stream.toString() should fullyMatch regex traceLog.format(": a prefix - a log with prefix")
  }

  "setLogPrefix" should "set logPrefix variable" in {
    val logger1 = GangLogger(isDTEnabled = false, logPrefix = Some("another prefix"), isHostnameEnabled = false)

    logger1.loggers.head.asInstanceOf[PrintlnLogger].loggerConfig.logPrefix shouldBe Some("another prefix")

    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger1.trace("another log with prefix")
    }
    stream.toString() should fullyMatch regex traceLog.format(": another prefix - another log with prefix")
  }

  "getLogger" should "return an existing GangLogger or a new GangLogger()" in {
    GangLogger.killLogger()
    val newLogger = GangLogger(logPrefix = Some("123"), isHostnameEnabled = false)
    val logger1 = GangLogger.getLogger
    val logger2 = GangLogger.getLogger
    logger1 shouldEqual logger2

    GangLogger.killLogger()
    val stream = new java.io.ByteArrayOutputStream()
    val logger3 = Console.withOut(stream) {
      GangLogger.getLogger
    }
    logger3 shouldEqual GangLogger._logger.get

    stream.toString() should fullyMatch regex
      infoLog.format("cn.tellyouwhat.gangsutils.logger.exceptions.NoAliveLoggerException: " + I18N.getRB.getString("getLogger.NoAliveLogger"))
  }

  "killLogger" should "reset the _logger variable to None" in {
    GangLogger()
    GangLogger._logger shouldNot be(None)

    GangLogger.killLogger()
    GangLogger._logger shouldBe None
  }

}
