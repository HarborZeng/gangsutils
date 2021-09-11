package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.retry
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.FEISHU_WEBHOOK_LOGGER
import cn.tellyouwhat.gangsutils.logger.cc.{LoggerConfiguration, Robot}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class FeishuWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    GangLogger.setLoggerAndConfiguration(Map(
      FEISHU_WEBHOOK_LOGGER -> LoggerConfiguration()
    ))
  }

  after {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
    FeishuWebhookLogger.resetRobots()
    FeishuWebhookLogger.resetConfiguration()
    // rate limit
    Thread.sleep(500)
  }

  behavior of "FeishuWebhookLoggerTest"

  it should "initializeFeishuWebhook(robotsKeysSigns: String)" in {
    the[NullPointerException] thrownBy {
      FeishuWebhookLogger.initializeFeishuWebhook(null: String)
    } should have message null

    FeishuWebhookLogger.initializeFeishuWebhook("abc,def")
    val logger1 = GangLogger()
    logger1.loggers.head.asInstanceOf[FeishuWebhookLogger].feishuRobotsToSend should contain theSameElementsAs Seq(Robot(Some("abc"), None), Robot(Some("def"), None))
    FeishuWebhookLogger.initializeFeishuWebhook("abc;123,def")
    val logger2 = GangLogger()
    logger2.loggers.head.asInstanceOf[FeishuWebhookLogger].feishuRobotsToSend should contain theSameElementsAs Seq(Robot(Some("abc"), Some("123")), Robot(Some("def"), None))
    FeishuWebhookLogger.initializeFeishuWebhook("abc")
    val logger3 = GangLogger()
    logger3.loggers.head.asInstanceOf[FeishuWebhookLogger].feishuRobotsToSend should contain theSameElementsAs Seq(Robot(Some("abc"), None))
  }

  it should "initializeFeishuWebhook(robotsKeysSigns: Array[Array[String]])" in {
    the[IllegalArgumentException] thrownBy {
      FeishuWebhookLogger.initializeFeishuWebhook("")
    } should have message I18N.getRB.getString("feishuWebhookLogger.initializeFeishuWebhook").format("Array(Array())")
    the[IllegalArgumentException] thrownBy {
      FeishuWebhookLogger.initializeFeishuWebhook("123,,abc")
    } should have message I18N.getRB.getString("feishuWebhookLogger.initializeFeishuWebhook").format("Array(Array(123), Array(), Array(abc))")
    the[IllegalArgumentException] thrownBy {
      FeishuWebhookLogger.initializeFeishuWebhook("123,,abc;123;234")
    } should have message I18N.getRB.getString("feishuWebhookLogger.initializeFeishuWebhook").format("Array(Array(123), Array(), Array(abc, 123, 234))")
    the[IllegalArgumentException] thrownBy {
      FeishuWebhookLogger.initializeFeishuWebhook(null: Array[Array[String]])
    } should have message I18N.getRB.getString("feishuWebhookLogger.initializeFeishuWebhook").format("null")
    the[IllegalArgumentException] thrownBy {
      FeishuWebhookLogger.initializeFeishuWebhook(Array.empty[Array[String]])
    } should have message I18N.getRB.getString("feishuWebhookLogger.initializeFeishuWebhook").format("Array()")
  }

  "feishu webhook logger" should "send a log into feishu with correct key and sign" in {
    FeishuWebhookLogger.initializeFeishuWebhook("085380aa-4d07-4ecc-b17f-fbb978e1da72;BRH2wOO3SOi64Sw0wiMXtb")
    val logger = GangLogger()
    retry(2)(logger.info("feishu webhook logger send a log into feishu with correct key and sign")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "send a log into feishu with correct key" in {
    FeishuWebhookLogger.initializeFeishuWebhook("040117de-7776-444b-ba61-9bbee3ad5e33")
    val logger = GangLogger()
    retry(2)(logger.info("feishu webhook logger send a log into feishu with correct key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into feishu with incorrect key" in {
    FeishuWebhookLogger.initializeFeishuWebhook("a3af")
    val logger = GangLogger()
    retry(2)(logger.info("feishu webhook logger not send a log into feishu with incorrect key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe false
    }
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if robotsToSend is empty" in {
    val logger = GangLogger()
    an[IllegalArgumentException] should be thrownBy logger.info("")
  }

  "FeishuWebhookLogger" should "be newed with an IllegalArgumentException thrown if loggerConfig was not set" in {
    the [IllegalArgumentException] thrownBy new FeishuWebhookLogger() should have message "FeishuWebhookLogger.loggerConfig is None"
  }

  it should "be applied with an IllegalArgumentException thrown if initializeConfiguration(c: LoggerConfiguration) or apply(c: LoggerConfiguration) was not set" in {
    the [IllegalArgumentException] thrownBy FeishuWebhookLogger() should have message "You did not pass parameter loggerConfig nor initializeConfiguration"
  }
}
