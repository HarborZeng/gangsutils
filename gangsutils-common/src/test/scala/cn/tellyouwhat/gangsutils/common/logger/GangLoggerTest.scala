package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.exceptions.GangException
import cn.tellyouwhat.gangsutils.common.logger.SupportedLogDest.{PRINTLN_LOGGER, WOA_WEBHOOK_LOGGER}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GangLoggerTest extends AnyFlatSpec with Matchers {

  behavior of "GangLoggerTest"

  val logger: BaseLogger = GangLogger(isDTEnabled = false, isTraceEnabled = false)

  it should "success" in {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.success("a success log")
    }
    stream.toString should fullyMatch regex """\u001b\[32m【成功】: a success log\u001b\[0m\s+""".r
  }

  it should "info" in {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.info("an info log")
    }
    stream.toString should fullyMatch regex """\u001b\[1m【信息】: an info log\u001b\[0m\s+""".r
  }

  it should "trace" in {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.trace("a trace log")
    }
    stream.toString should fullyMatch regex """【跟踪】: a trace log\s+""".r
  }

  it should "log" in {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.log("a log", level = LogLevel.TRACE)
    }
    stream.toString() should fullyMatch regex """【跟踪】: a log\s+""".r
  }

  it should "critical" in {
    val stream = new java.io.ByteArrayOutputStream()
    an [GangException] should be thrownBy {
      Console.withOut(stream) {
        logger.critical("a critical log")
      }
    }
    stream.toString should fullyMatch regex """\u001b\[31m\u001b\[1m【致命】: a critical log\u001b\[0m\s+""".r
  }

  it should "warning" in {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.warning("a warning log")
    }
    stream.toString should fullyMatch regex """\u001b\[33m【警告】: a warning log\u001b\[0m\s+""".r
  }

  it should "error" in {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.error("an error log")
    }
    stream.toString should fullyMatch regex """\u001b\[31m【错误】: an error log\u001b\[0m\s+""".r
  }

  it should "setLogsLevels(levels: Array[LogLevel.Value])" in {
    an [IllegalArgumentException] should be thrownBy GangLogger.setLogsLevels(Array.empty[LogLevel.Value])
    a [NullPointerException] should be thrownBy GangLogger.setLogsLevels(null: Array[LogLevel.Value])

    GangLogger.setLogsLevels(Array.fill(2)(LogLevel.TRACE))
    val levels = GangLogger().logsLevels
    levels should contain theSameElementsAs Array.fill(2)(LogLevel.TRACE)
  }

  it should "setLogsLevels(levels: Map[SupportedLogDest.Value, LogLevel.Value])" in {
    an [IllegalArgumentException] should be thrownBy GangLogger.setLogsLevels(Map.empty[SupportedLogDest.Value, LogLevel.Value])
    an [IllegalArgumentException] should be thrownBy GangLogger.setLogsLevels(null: Map[SupportedLogDest.Value, LogLevel.Value])

    GangLogger.setLogsLevels(Map(PRINTLN_LOGGER -> LogLevel.TRACE, WOA_WEBHOOK_LOGGER -> LogLevel.INFO))
    val levels = GangLogger().logsLevels
    levels should contain theSameElementsAs Array(LogLevel.TRACE, LogLevel.INFO)
  }

  it should "setDefaultLogDest" in {
    GangLogger.setDefaultLogDest(Seq(SupportedLogDest.WOA_WEBHOOK_LOGGER, SupportedLogDest.PRINTLN_LOGGER))
    val dest = GangLogger().defaultLogDest
    dest should have size 2
    dest should contain (SupportedLogDest.WOA_WEBHOOK_LOGGER)
    dest should contain (SupportedLogDest.PRINTLN_LOGGER)
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

  it should "disable/enableDateTime" in {
    GangLogger.disableDateTime()
    GangLogger().isDTEnabled shouldBe false
    GangLogger.enableDateTime()
    GangLogger().isDTEnabled shouldBe true
    GangLogger.disableDateTime()
    GangLogger().isDTEnabled shouldBe false
  }

  it should "apply with logPrefix" in {
    val logger2 = GangLogger(isDTEnabled = false, logPrefix = "a prefix")
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger2.trace("a log with prefix")
    }
    stream.toString() should fullyMatch regex """【跟踪】: a prefix - a log with prefix\s+""".r
  }

  "setLogPrefix" should "set logPrefix variable" in {
    GangLogger.setLogPrefix("another prefix")
    val logger1 = GangLogger(isDTEnabled = false)

    logger1.logPrefix shouldBe "another prefix"

    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      logger1.trace("another log with prefix")
    }
    stream.toString() should fullyMatch regex """【跟踪】: another prefix - another log with prefix\s+""".r
  }

}
