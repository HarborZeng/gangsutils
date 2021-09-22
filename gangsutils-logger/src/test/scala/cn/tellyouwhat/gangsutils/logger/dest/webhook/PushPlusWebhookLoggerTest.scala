package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.retry
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.PUSHPULS_WEBHOOK_LOGGER
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class PushPlusWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    GangLogger.setLoggerAndConfiguration(Map(
      PUSHPULS_WEBHOOK_LOGGER -> LoggerConfiguration()
    ))
  }

  after {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
    PushPlusWebhookLogger.resetRobotsKeys()
    PushPlusWebhookLogger.resetConfiguration()
    PushPlusWebhookLogger.clearProxy()
    // rate limit to prevent 系统繁忙 error
    Thread.sleep(1000)
  }

  behavior of "PushPlusWebhookLoggerTest"

  it should "initializePushplusWebhook(robotsKeys: String)" in {
    the[NullPointerException] thrownBy PushPlusWebhookLogger.initializePushplusWebhook(null: String) should have message null
    PushPlusWebhookLogger.initializePushplusWebhook("abc,def")
    the[IllegalArgumentException] thrownBy GangLogger() should have message "You haven't invoke setLoggerTemplate(template: String)"
    PushPlusWebhookLogger.setLoggerTemplate("html")
    val logger1 = GangLogger()
    logger1.loggers.head.asInstanceOf[PushPlusWebhookLogger].pushplusRobotsToSend should contain theSameElementsAs Seq("abc", "def")
    PushPlusWebhookLogger.initializePushplusWebhook("abc")
    val logger2 = GangLogger()
    logger2.loggers.head.asInstanceOf[PushPlusWebhookLogger].pushplusRobotsToSend should contain theSameElementsAs Seq("abc")
  }

  it should "initializePushplusWebhook(robotsKeys: Array[String])" in {
    the[IllegalArgumentException] thrownBy {
      PushPlusWebhookLogger.initializePushplusWebhook("")
    } should have message I18N.getRB.getString("pushplusWebhookLogger.initializePushplusWebhook").format("Array()")
    the[IllegalArgumentException] thrownBy {
      PushPlusWebhookLogger.initializePushplusWebhook("123,,abc")
    } should have message I18N.getRB.getString("pushplusWebhookLogger.initializePushplusWebhook").format("Array(123, , abc)")
    the[NullPointerException] thrownBy {
      PushPlusWebhookLogger.initializePushplusWebhook(null: Array[String])
    } should have message null
    the[IllegalArgumentException] thrownBy {
      PushPlusWebhookLogger.initializePushplusWebhook(Array.empty[String])
    } should have message I18N.getRB.getString("pushplusWebhookLogger.initializePushplusWebhook").format("Array()")
  }

  "pushplus webhook logger" should "send a log into pushplus with correct key in html format" in {
    PushPlusWebhookLogger.initializePushplusWebhook("17930244e49a4fbb96358359f85b35c1")
    PushPlusWebhookLogger.setLoggerTemplate("html")
    val logger = GangLogger()
    retry(2)(logger.warning("pushplus webhook logger send a log into pushplus with correct key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  "pushplus webhook logger" should "send a log into pushplus with correct key in json format" in {
    GangLogger.setLoggerAndConfiguration(Map(
      PUSHPULS_WEBHOOK_LOGGER -> LoggerConfiguration(isDTEnabled = true, isTraceEnabled = true, isHostnameEnabled = true, logPrefix = Some("the prefix"))
    ))
    PushPlusWebhookLogger.initializePushplusWebhook("17930244e49a4fbb96358359f85b35c1")
    PushPlusWebhookLogger.setLoggerTemplate("json")
    val logger = GangLogger()
    logger.loggers.head.asInstanceOf[PushPlusWebhookLogger].loggerConfig
    retry(2)(logger.warning("pushplus webhook logger send a log into pushplus with correct key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  "pushplus webhook logger" should "send a log into pushplus with correct key in plaintext" in {
    GangLogger.setLoggerAndConfiguration(Map(
      PUSHPULS_WEBHOOK_LOGGER -> LoggerConfiguration(isDTEnabled = true, isTraceEnabled = true, isHostnameEnabled = true, logPrefix = Some("the prefix"))
    ))
    PushPlusWebhookLogger.initializePushplusWebhook("17930244e49a4fbb96358359f85b35c1")
    PushPlusWebhookLogger.setLoggerTemplate("plaintext")
    val logger = GangLogger()
    logger.loggers.head.asInstanceOf[PushPlusWebhookLogger].loggerConfig
    retry(2)(logger.warning("pushplus webhook logger send a log into pushplus with correct key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  "pushplus webhook logger" should "send a log into pushplus gangsutils topic with correct key in plaintext" in {
    GangLogger.setLoggerAndConfiguration(Map(
      PUSHPULS_WEBHOOK_LOGGER -> LoggerConfiguration(isDTEnabled = true, isTraceEnabled = true, isHostnameEnabled = true, logPrefix = Some("the prefix"))
    ))
    PushPlusWebhookLogger.initializePushplusWebhook("17930244e49a4fbb96358359f85b35c1")
    PushPlusWebhookLogger.setLoggerTemplate("plaintext")
    PushPlusWebhookLogger.setLoggerTopic("gangsutils")
    val logger = GangLogger()
    retry(2)(logger.warning("pushplus webhook logger send a log into pushplus gangsutils topic with correct key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into pushplus with incorrect key" in {
    PushPlusWebhookLogger.initializePushplusWebhook("a3af")
    PushPlusWebhookLogger.setLoggerTemplate("html")
    val logger = GangLogger()
    retry(2)(logger.info("pushplus webhook logger not send a log into pushplus with incorrect key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe false
    }
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if robotsToSend is empty" in {
    val logger = GangLogger()
    an[IllegalArgumentException] should be thrownBy logger.info("")
  }

  "PushPlusWebhookLogger" should "be newed with an IllegalArgumentException thrown if loggerConfig was not set" in {
    the[IllegalArgumentException] thrownBy new PushPlusWebhookLogger() should have message "PushPlusWebhookLogger.loggerConfig is None"
  }

  it should "be applied with an IllegalArgumentException thrown if initializeConfiguration(c: LoggerConfiguration) or apply(c: LoggerConfiguration) was not set" in {
    the[IllegalArgumentException] thrownBy PushPlusWebhookLogger() should have message "You did not pass parameter loggerConfig nor initializeConfiguration"
  }
}
