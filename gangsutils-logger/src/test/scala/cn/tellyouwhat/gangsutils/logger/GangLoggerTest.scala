package cn.tellyouwhat.gangsutils.logger

import cn.tellyouwhat.gangsutils.core.constants.{criticalLog, errorLog, infoLog, successLog, traceLog, warningLog}
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.{PRINTLN_LOGGER, WOA_WEBHOOK_LOGGER}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}

class GangLoggerTest extends AnyFlatSpec with Matchers with PrivateMethodTester with BeforeAndAfter {

  before {
    GangLogger.disableDateTime()
    GangLogger.disableHostname()
  }

  after {
    GangLogger.killLogger()
    GangLogger.resetLoggerConfig()
  }

  behavior of "GangLoggerTest"

  it should "success" in {
    val logger: Logger = GangLogger(isDTEnabled = false, isTraceEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.success("a success log")
    }
    stream.toString should fullyMatch regex successLog.format(": a success log")
  }

  it should "info" in {
    val logger: Logger = GangLogger(isDTEnabled = false, isTraceEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.info("an info log")
    }
    stream.toString should fullyMatch regex infoLog.format(": an info log")
  }

  it should "trace" in {
    val logger: Logger = GangLogger(isDTEnabled = false, isTraceEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.trace("a trace log")
    }
    stream.toString should fullyMatch regex traceLog.format(": a trace log")
  }

  it should "log" in {
    val logger: Logger = GangLogger(isDTEnabled = false, isTraceEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.log("a log", level = LogLevel.TRACE)
    }
    stream.toString() should fullyMatch regex traceLog.format(": a log")
  }

  it should "critical" in {
    val logger: Logger = GangLogger(isDTEnabled = false, isTraceEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.critical("a critical log")
    }
    stream.toString should fullyMatch regex criticalLog.format(": a critical log")
  }

  it should "warning" in {
    val logger: Logger = GangLogger(isDTEnabled = false, isTraceEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.warning("a warning log")
    }
    stream.toString should fullyMatch regex warningLog.format(": a warning log")
  }

  it should "error" in {
    val logger: Logger = GangLogger(isDTEnabled = false, isTraceEnabled = false)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.error("an error log")
    }
    stream.toString should fullyMatch regex errorLog.format(": an error log")
  }

  it should "setLogsLevels(levels: Array[LogLevel.Value])" in {
    an[IllegalArgumentException] should be thrownBy GangLogger.setLogsLevels(Array.empty[LogLevel.Value])
    a[NullPointerException] should be thrownBy GangLogger.setLogsLevels(null: Array[LogLevel.Value])

    GangLogger.setLogsLevels(Array.fill(SupportedLogDest.maxId)(LogLevel.TRACE))
    val levels = GangLogger().logsLevels
    levels should contain theSameElementsAs Array.fill(SupportedLogDest.maxId)(LogLevel.TRACE)
  }

  it should "setLogsLevels(levels: Map[SupportedLogDest.Value, LogLevel.Value])" in {
    an[IllegalArgumentException] should be thrownBy GangLogger.setLogsLevels(Map.empty[SupportedLogDest.Value, LogLevel.Value])
    an[IllegalArgumentException] should be thrownBy GangLogger.setLogsLevels(null: Map[SupportedLogDest.Value, LogLevel.Value])

    GangLogger.setLogsLevels(Map(PRINTLN_LOGGER -> LogLevel.TRACE, WOA_WEBHOOK_LOGGER -> LogLevel.INFO))
    val levels = GangLogger().logsLevels
    levels should contain theSameElementsAs Array(LogLevel.TRACE, LogLevel.INFO) ++ Array.fill(SupportedLogDest.maxId - 2)(LogLevel.TRACE)
  }

  it should "setDefaultLogDest" in {
    GangLogger.setDefaultLogDest(Seq(SupportedLogDest.WOA_WEBHOOK_LOGGER, SupportedLogDest.PRINTLN_LOGGER))
    val dest = GangLogger().defaultLogDest
    dest should have size 2
    dest should contain(SupportedLogDest.WOA_WEBHOOK_LOGGER)
    dest should contain(SupportedLogDest.PRINTLN_LOGGER)
  }

  it should "apply" in {
    val logger1 = GangLogger.apply()
    val logger2 = new GangLogger()

    logger1.logsLevels should contain theSameElementsAs logger2.logsLevels
    logger1.defaultLogDest should contain theSameElementsAs logger2.defaultLogDest
  }

  it should "apply(isDTEnabled: Boolean = isDTEnabled,isTraceEnabled: Boolean = isTraceEnabled,defaultLogDest: Seq[SupportedLogDest.Value] = defaultLogDest,logsLevels: Array[LogLevel.Value] = logsLevels, logPrefix = logPrefix)" in {
    val logger1 = GangLogger.apply(isDTEnabled = false, isTraceEnabled = true, defaultLogDest = SupportedLogDest.values.toSeq, logsLevels = Array.fill(SupportedLogDest.maxId)(LogLevel.INFO))
    val logger2 = new GangLogger(isDTEnabled = false, isTraceEnabled = true, defaultLogDest = SupportedLogDest.values.toSeq, logsLevels = Array.fill(SupportedLogDest.maxId)(LogLevel.INFO))

    logger1.isDTEnabled shouldEqual logger2.isDTEnabled
    logger1.isTraceEnabled shouldEqual logger2.isTraceEnabled
    logger1.logsLevels should contain theSameElementsAs logger2.logsLevels
    logger1.defaultLogDest should contain theSameElementsAs logger2.defaultLogDest
  }

  it should "disable/enableTrace" in {
    GangLogger.disableTrace()
    GangLogger().isTraceEnabled shouldBe false
    GangLogger.enableTrace()
    GangLogger().isTraceEnabled shouldBe true
    GangLogger.disableTrace()
    GangLogger().isTraceEnabled shouldBe false
  }

  it should "disable/enableHostname" in {
    GangLogger.disableHostname()
    GangLogger().isHostnameEnabled shouldBe false
    GangLogger.enableHostname()
    GangLogger().isHostnameEnabled shouldBe true
    GangLogger.disableHostname()
    GangLogger().isHostnameEnabled shouldBe false
  }

  it should "disable/enableDateTime" in {
    GangLogger.disableDateTime()
    GangLogger().isDTEnabled shouldBe false
    GangLogger.enableDateTime()
    val logger3 = GangLogger()
    logger3.isDTEnabled shouldBe true
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger3.trace("logger3 info")
    }
    stream.toString() should fullyMatch regex traceLog.format(""" - \d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d+: logger3 info""")
    GangLogger.disableDateTime()
    GangLogger().isDTEnabled shouldBe false
  }

  it should "apply with logPrefix" in {
    val logger2 = GangLogger(isDTEnabled = false, logPrefix = Some("a prefix"))
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger2.trace("a log with prefix")
    }
    stream.toString() should fullyMatch regex traceLog.format(": a prefix - a log with prefix")
  }

  "setLogPrefix" should "set logPrefix variable" in {
    GangLogger.setLogPrefix("another prefix")
    val logger1 = GangLogger(isDTEnabled = false)

    logger1.logPrefix shouldBe Some("another prefix")

    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger1.trace("another log with prefix")
    }
    stream.toString() should fullyMatch regex traceLog.format(": another prefix - another log with prefix")
  }

  "clearLogPrefix" should "reset logPrefix variable to default empty string" in {
    GangLogger.setLogPrefix("another prefix")
    val logger1 = GangLogger()

    GangLogger.clearLogPrefix()
    val logger2 = GangLogger()

    logger1.logPrefix shouldBe Some("another prefix")
    logger2.logPrefix shouldBe None
  }

  "getLogger" should "return an existing GangLogger or a new GangLogger()" in {
    val newLogger = GangLogger(logPrefix = Some("123"))
    val logger1 = GangLogger.getLogger
    logger1 shouldEqual newLogger

    GangLogger._logger = None
    val stream = new java.io.ByteArrayOutputStream()
    val logger2 = Console.withOut(stream) {
      GangLogger.getLogger
    }
    logger2 shouldEqual GangLogger._logger.get

    stream.toString() should fullyMatch regex
      warningLog.format(": cn.tellyouwhat.gangsutils.logger.exceptions.NoAliveLoggerException: " + I18N.getRB.getString("getLogger.NoAliveLogger"))
  }

  "killLogger" should "reset the _logger variable to None" in {
    GangLogger()
    GangLogger._logger shouldNot be(None)

    GangLogger.killLogger()
    GangLogger._logger shouldBe None
  }

}
