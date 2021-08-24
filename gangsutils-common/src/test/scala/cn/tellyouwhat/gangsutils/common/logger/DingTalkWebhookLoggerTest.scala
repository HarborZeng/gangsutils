package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.gangfunctions.retry
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class DingTalkWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    GangLogger.resetLoggerConfig()
  }

  after {
    DingTalkWebhookLogger.resetRobots()
  }

  behavior of "DingTalkWebhookLoggerTest"

  it should "initializeDingTalkWebhook(robotsKeysSigns: String)" in {
    a [NullPointerException] should be thrownBy DingTalkWebhookLogger.initializeDingTalkWebhook(null: String)
    DingTalkWebhookLogger.initializeDingTalkWebhook("abc,def")
    GangLogger().dingTalkRobotsToSend should contain theSameElementsAs Seq(DingTalkRobot(Some("abc"), None), DingTalkRobot(Some("def"), None))
    DingTalkWebhookLogger.initializeDingTalkWebhook("abc;123,def")
    GangLogger().dingTalkRobotsToSend should contain theSameElementsAs Seq(DingTalkRobot(Some("abc"), Some("123")), DingTalkRobot(Some("def"), None))
    DingTalkWebhookLogger.initializeDingTalkWebhook("abc")
    GangLogger().dingTalkRobotsToSend should contain theSameElementsAs Seq(DingTalkRobot(Some("abc"), None))
  }

  it should "initializeDingTalkWebhook(robotsKeysSigns: Array[Array[String]])" in {
    an [IllegalArgumentException] should be thrownBy DingTalkWebhookLogger.initializeDingTalkWebhook("")
    an [IllegalArgumentException] should be thrownBy DingTalkWebhookLogger.initializeDingTalkWebhook("123,,abc")
    an [IllegalArgumentException] should be thrownBy DingTalkWebhookLogger.initializeDingTalkWebhook("123,,abc;123;234")
    an [IllegalArgumentException] should be thrownBy DingTalkWebhookLogger.initializeDingTalkWebhook(null: Array[Array[String]])
    an [IllegalArgumentException] should be thrownBy DingTalkWebhookLogger.initializeDingTalkWebhook(Array.empty[Array[String]])
  }

  "dingtalk webhook logger" should "send a log into dingtalk with correct key and sign" in {
    DingTalkWebhookLogger.initializeDingTalkWebhook("b50b785dcba656265195521be1dd5accc9dadc5cb461dcda37d73a2dc86f309d;SEC26320f0a940219f49ab1858fee0eafbd4b5b4ff5da8e92e13ffdb5b57d91753b")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.DINGTALK_WEBHOOK_LOGGER))
    retry(5)(logger.info("dingtalk webhook logger send a log into dingtalk with correct key and sign")) match {
      case Failure(e) => a [SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "send a log into dingtalk with correct key" in {
    DingTalkWebhookLogger.initializeDingTalkWebhook("0ff315d9238ebe9a0a1bb229610e0434001a5998f5adf6a597887e48ddf0f270")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.DINGTALK_WEBHOOK_LOGGER))
    retry(5)(logger.info("dingtalk webhook logger send a log into dingtalk with correct key")) match {
      case Failure(e) => a [SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into dingtalk with incorrect key" in {
    DingTalkWebhookLogger.initializeDingTalkWebhook("a3af")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.DINGTALK_WEBHOOK_LOGGER))
    logger.info("dingtalk webhook logger not send a log into dingtalk with incorrect key") shouldBe true
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if robotsToSend is empty" in {
    val logger = GangLogger(defaultLogDest = SupportedLogDest.DINGTALK_WEBHOOK_LOGGER :: Nil)
    an [IllegalArgumentException] should be thrownBy { logger.info() }
  }

}
