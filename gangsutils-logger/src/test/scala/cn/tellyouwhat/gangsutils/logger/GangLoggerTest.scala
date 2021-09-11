package cn.tellyouwhat.gangsutils.logger

import cn.tellyouwhat.gangsutils.core.constants._
import cn.tellyouwhat.gangsutils.core.exceptions.GangException
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.{DINGTALK_WEBHOOK_LOGGER, PRINTLN_LOGGER, WOA_WEBHOOK_LOGGER}
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.dest.PrintlnLogger
import cn.tellyouwhat.gangsutils.logger.dest.webhook.WoaWebhookLogger
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, PrivateMethodTester}

import java.io.ByteArrayOutputStream
import java.net.InetAddress

class GangLoggerTest extends AnyFlatSpec with Matchers with PrivateMethodTester with BeforeAndAfter with BeforeAndAfterAll {

  val stream = new ByteArrayOutputStream()

  before {
    GangLogger.clearLogger2Configuration()
  }
  after {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
    stream.reset()
  }

  override protected def afterAll(): Unit = {
    stream.close()
  }

  behavior of "GangLoggerTest"

  it should "success" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    Console.withOut(stream) {
      logger.success("a success log")
    }
    stream.toString should fullyMatch regex successLog.format(": a success log")
  }

  it should "info" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    Console.withOut(stream) {
      logger.info("an info log")
    }
    stream.toString should fullyMatch regex infoLog.format(": an info log")
  }

  it should "trace" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    Console.withOut(stream) {
      logger.trace("a trace log")
    }
    stream.toString should fullyMatch regex traceLog.format(": a trace log")
  }

  it should "log" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    Console.withOut(stream) {
      logger.log("a log", None, level = LogLevel.TRACE)
    }
    stream.toString() should fullyMatch regex traceLog.format(": a log")
  }

  it should "critical" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    Console.withOut(stream) {
      logger.critical("a critical log")
    }
    stream.toString should fullyMatch regex criticalLog.format(": a critical log")
  }

  it should "warning" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    Console.withOut(stream) {
      logger.warning("a warning log")
    }
    stream.toString should fullyMatch regex warningLog.format(": a warning log")
  }

  it should "error" in {
    val logger = GangLogger(isDTEnabled = false, isTraceEnabled = false, isHostnameEnabled = false)
    Console.withOut(stream) {
      logger.error("an error log")
    }
    stream.toString should fullyMatch regex errorLog.format(": an error log")
  }

  it should "trace with enabled parameter" in {
    WoaWebhookLogger.initializeWoaWebhook("abc")
    GangLogger.setLoggerAndConfiguration(Map(
      PRINTLN_LOGGER -> LoggerConfiguration(isDTEnabled = false, isHostnameEnabled = false),
      WOA_WEBHOOK_LOGGER -> LoggerConfiguration(),
    ))
    val logger = GangLogger()
    Console.withOut(stream) {
      logger.trace("l")(enabled = DINGTALK_WEBHOOK_LOGGER :: Nil)
    }
    stream.toString should fullyMatch regex errorLog.format(
      """: Specified log destination Vector\(cn.tellyouwhat.gangsutils.logger.dest.webhook.DingTalkWebhookLogger\) in Vector\(cn.tellyouwhat.gangsutils.logger.dest.webhook.DingTalkWebhookLogger\) does not support, supported are Vector\(cn.tellyouwhat.gangsutils.logger.dest.PrintlnLogger, cn.tellyouwhat.gangsutils.logger.dest.webhook.WoaWebhookLogger\)"""
    )
    stream.close()
    WoaWebhookLogger.resetConfiguration()
    WoaWebhookLogger.resetRobotsKeys()
  }

  it should "apply with logPrefix" in {
    val logger2 = GangLogger(isDTEnabled = false, logPrefix = Some("a prefix"), isHostnameEnabled = false)
    Console.withOut(stream) {
      logger2.trace("a log with prefix")
    }
    stream.toString() should fullyMatch regex traceLog.format(": a prefix - a log with prefix")
  }

  it should "apply with hostname" in {
    val logger = GangLogger(isDTEnabled = false, isHostnameEnabled = true)
    Console.withOut(stream) {
      logger.trace("a log with hostname")
    }
    stream.toString() should fullyMatch regex traceLog.format(s" - ${InetAddress.getLocalHost.getHostName}: a log with hostname")
  }

  it should "log with exception stack trace" in {
    val logger = GangLogger(isDTEnabled = false, isHostnameEnabled = false)
    Console.withOut(stream) {
      logger.trace("a log with stacktrace", GangException("reason"))
    }
    stream.toString() should fullyMatch regex
      traceLog.format(s": a log with stacktrace") +
        redPattern + """Exception in thread "[^"]+" [^:]+: reason\s+""" + resetPattern + """\s+"""
  }

  "setLogPrefix" should "set logPrefix variable" in {
    val logger1 = GangLogger(isDTEnabled = false, logPrefix = Some("another prefix"), isHostnameEnabled = false)

    logger1.loggers.head.asInstanceOf[PrintlnLogger].loggerConfig.logPrefix shouldBe Some("another prefix")

    Console.withOut(stream) {
      logger1.trace("another log with prefix")
    }
    stream.toString() should fullyMatch regex traceLog.format(": another prefix - another log with prefix")
  }

  "getLogger" should "return an existing GangLogger or a new GangLogger()" in {
    GangLogger.killLogger()
    val newLogger = GangLogger(logPrefix = Some("123"), isHostnameEnabled = false)
    val logger1 = GangLogger.getLogger
    newLogger shouldEqual logger1

    GangLogger.killLogger()
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

  "setLoggerAndConfiguration" should "throw exceptions when m is illegal" in {
    the[IllegalArgumentException] thrownBy {
      GangLogger.setLoggerAndConfiguration(null: Map[SupportedLogDest.Value, LoggerConfiguration])
    } should have message "null parameter"

    the[IllegalArgumentException] thrownBy {
      GangLogger.setLoggerAndConfiguration(Map.empty[SupportedLogDest.Value, LoggerConfiguration])
    } should have message "empty parameter"

  }
  
  it should "throw exceptions when s is illegal" in {
    the[IllegalArgumentException] thrownBy {
      GangLogger.setLoggerAndConfiguration(null: Seq[(SupportedLogDest.Value, LoggerConfiguration)])
    } should have message "null parameter"

    the[IllegalArgumentException] thrownBy {
      GangLogger.setLoggerAndConfiguration(Seq.empty[(SupportedLogDest.Value, LoggerConfiguration)])
    } should have message "empty parameter"

  }
}
