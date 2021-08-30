package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.retry
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.DINGTALK_WEBHOOK_LOGGER
import cn.tellyouwhat.gangsutils.logger.cc.{LoggerConfiguration, Robot}
import org.mockito.MockitoSugar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class DingTalkWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter with MockitoSugar with PrivateMethodTester {

  before {
    GangLogger.setLoggerAndConfiguration(Map(
      DINGTALK_WEBHOOK_LOGGER -> LoggerConfiguration()
    ))
  }
  after {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
    DingTalkWebhookLogger.resetRobots()
    DingTalkWebhookLogger.resetConfiguration()
  }


  behavior of "DingTalkWebhookLoggerTest"

  it should "initializeDingTalkWebhook(robotsKeysSigns: String)" in {
    the[NullPointerException] thrownBy {
      DingTalkWebhookLogger.initializeDingTalkWebhook(null: String)
    } should have message null

    DingTalkWebhookLogger.initializeDingTalkWebhook("abc,def")
    val logger1 = GangLogger()
    logger1.loggers.head.asInstanceOf[DingTalkWebhookLogger].dingTalkRobotsToSend should contain theSameElementsAs Seq(Robot(Some("abc"), None), Robot(Some("def"), None))

    DingTalkWebhookLogger.initializeDingTalkWebhook("abc;123,def")
    val logger2 = GangLogger()
    logger2.loggers.head.asInstanceOf[DingTalkWebhookLogger].dingTalkRobotsToSend should contain theSameElementsAs Seq(Robot(Some("abc"), Some("123")), Robot(Some("def"), None))

    DingTalkWebhookLogger.initializeDingTalkWebhook("abc")
    val logger3 = GangLogger()
    logger3.loggers.head.asInstanceOf[DingTalkWebhookLogger].dingTalkRobotsToSend should contain theSameElementsAs Seq(Robot(Some("abc"), None))
  }

  it should "initializeDingTalkWebhook(robotsKeysSigns: Array[Array[String]])" in {
    the[IllegalArgumentException] thrownBy {
      DingTalkWebhookLogger.initializeDingTalkWebhook("")
    } should have message I18N.getRB.getString("dingTalkWebhookLogger.initializeDingTalkWebhook").format("Array(Array())")

    the[IllegalArgumentException] thrownBy {
      DingTalkWebhookLogger.initializeDingTalkWebhook("123,,abc")
    } should have message I18N.getRB.getString("dingTalkWebhookLogger.initializeDingTalkWebhook").format("Array(Array(123), Array(), Array(abc))")

    the[IllegalArgumentException] thrownBy {
      DingTalkWebhookLogger.initializeDingTalkWebhook("123,,abc;123;234")
    } should have message I18N.getRB.getString("dingTalkWebhookLogger.initializeDingTalkWebhook").format("Array(Array(123), Array(), Array(abc, 123, 234))")

    the[IllegalArgumentException] thrownBy {
      DingTalkWebhookLogger.initializeDingTalkWebhook(null: Array[Array[String]])
    } should have message I18N.getRB.getString("dingTalkWebhookLogger.initializeDingTalkWebhook").format("null")

    the[IllegalArgumentException] thrownBy {
      DingTalkWebhookLogger.initializeDingTalkWebhook(Array.empty[Array[String]])
    } should have message I18N.getRB.getString("dingTalkWebhookLogger.initializeDingTalkWebhook").format("Array()")
  }

  "dingtalk webhook logger" should "send a log into dingtalk with correct key and sign" in {
    DingTalkWebhookLogger.initializeDingTalkWebhook("b50b785dcba656265195521be1dd5accc9dadc5cb461dcda37d73a2dc86f309d;SEC26320f0a940219f49ab1858fee0eafbd4b5b4ff5da8e92e13ffdb5b57d91753b")

    val logger = GangLogger()
    retry(2)(logger.info("dingtalk webhook logger send a log into dingtalk with correct key and sign")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "send a log into dingtalk with correct key" in {
    DingTalkWebhookLogger.initializeDingTalkWebhook("0ff315d9238ebe9a0a1bb229610e0434001a5998f5adf6a597887e48ddf0f270")
    val logger = GangLogger()
    retry(2)(logger.info("dingtalk webhook logger send a log into dingtalk with correct key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into dingtalk with incorrect key" in {
    DingTalkWebhookLogger.initializeDingTalkWebhook("a3af")
    val logger = GangLogger()
    retry(2)(logger.info("dingtalk webhook logger not send a log into dingtalk with incorrect key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe false
    }
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if robotsToSend is empty" in {
    val logger = GangLogger()
    an[IllegalArgumentException] should be thrownBy {
      logger.info()
    }
  }

  "DingTalkWebhookLogger" should "be newed with an IllegalArgumentException thrown if loggerConfig was not set" in {
    the [IllegalArgumentException] thrownBy new DingTalkWebhookLogger() should have message "DingTalkWebhookLogger.loggerConfig is None"
  }

  it should "be applied with an IllegalArgumentException thrown if initializeConfiguration(c: LoggerConfiguration) or apply(c: LoggerConfiguration) was not set" in {
    the [IllegalArgumentException] thrownBy DingTalkWebhookLogger() should have message "You did not pass parameter loggerConfig nor initializeConfiguration"
  }
}
