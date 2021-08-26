package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.retry
import cn.tellyouwhat.gangsutils.logger.cc.Robot
import cn.tellyouwhat.gangsutils.logger.{GangLogger, SupportedLogDest}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class FeishuWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    GangLogger.resetLoggerConfig()
  }

  after {
    FeishuWebhookLogger.resetRobots()
  }

  behavior of "FeishuWebhookLoggerTest"

  it should "initializeFeishuWebhook(robotsKeysSigns: String)" in {
    a[NullPointerException] should be thrownBy FeishuWebhookLogger.initializeFeishuWebhook(null: String)
    FeishuWebhookLogger.initializeFeishuWebhook("abc,def")
    GangLogger().feishuRobotsToSend should contain theSameElementsAs Seq(new Robot(Some("abc"), None), new Robot(Some("def"), None))
    FeishuWebhookLogger.initializeFeishuWebhook("abc;123,def")
    GangLogger().feishuRobotsToSend should contain theSameElementsAs Seq(new Robot(Some("abc"), Some("123")), new Robot(Some("def"), None))
    FeishuWebhookLogger.initializeFeishuWebhook("abc")
    GangLogger().feishuRobotsToSend should contain theSameElementsAs Seq(new Robot(Some("abc"), None))
  }

  it should "initializeFeishuWebhook(robotsKeysSigns: Array[Array[String]])" in {
    an[IllegalArgumentException] should be thrownBy FeishuWebhookLogger.initializeFeishuWebhook("")
    an[IllegalArgumentException] should be thrownBy FeishuWebhookLogger.initializeFeishuWebhook("123,,abc")
    an[IllegalArgumentException] should be thrownBy FeishuWebhookLogger.initializeFeishuWebhook("123,,abc;123;234")
    an[IllegalArgumentException] should be thrownBy FeishuWebhookLogger.initializeFeishuWebhook(null: Array[Array[String]])
    an[IllegalArgumentException] should be thrownBy FeishuWebhookLogger.initializeFeishuWebhook(Array.empty[Array[String]])
  }

  "feishu webhook logger" should "send a log into feishu with correct key and sign" in {
    FeishuWebhookLogger.initializeFeishuWebhook("085380aa-4d07-4ecc-b17f-fbb978e1da72;BRH2wOO3SOi64Sw0wiMXtb")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.FEISHU_WEBHOOK_LOGGER))
    retry(2)(logger.info("feishu webhook logger send a log into feishu with correct key and sign")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "send a log into feishu with correct key" in {
    FeishuWebhookLogger.initializeFeishuWebhook("040117de-7776-444b-ba61-9bbee3ad5e33")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.FEISHU_WEBHOOK_LOGGER))
    retry(2)(logger.info("feishu webhook logger send a log into feishu with correct key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into feishu with incorrect key" in {
    FeishuWebhookLogger.initializeFeishuWebhook("a3af")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.FEISHU_WEBHOOK_LOGGER))
    retry(2)(logger.info("feishu webhook logger not send a log into feishu with incorrect key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe false
    }
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if robotsToSend is empty" in {
    val logger = GangLogger(defaultLogDest = SupportedLogDest.FEISHU_WEBHOOK_LOGGER :: Nil)
    an[IllegalArgumentException] should be thrownBy logger.info()
  }

}
