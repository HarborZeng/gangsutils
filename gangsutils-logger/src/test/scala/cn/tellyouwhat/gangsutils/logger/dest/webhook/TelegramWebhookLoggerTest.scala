package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.retry
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.TELEGRAM_WEBHOOK_LOGGER
import cn.tellyouwhat.gangsutils.logger.cc.{LoggerConfiguration, TelegramRobot}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class TelegramWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    GangLogger.setLoggerAndConfiguration(Map(
      TELEGRAM_WEBHOOK_LOGGER -> LoggerConfiguration()
    ))
  }

  after {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
    TelegramWebhookLogger.resetRobots()
    TelegramWebhookLogger.resetConfiguration()
    TelegramWebhookLogger.clearProxy()
    // rate limit
    Thread.sleep(500)
  }

  behavior of "TelegramWebhookLoggerTest"

  it should "initializeTelegramWebhook(robotsChatIdsTokens: String)" in {
    the[NullPointerException] thrownBy TelegramWebhookLogger.initializeTelegramWebhook(null: String) should have message null
    an[IllegalArgumentException] should be thrownBy TelegramWebhookLogger.initializeTelegramWebhook("abc,def")
    an[IllegalArgumentException] should be thrownBy TelegramWebhookLogger.initializeTelegramWebhook("abc;123,def")
    an[IllegalArgumentException] should be thrownBy TelegramWebhookLogger.initializeTelegramWebhook("abc")
    TelegramWebhookLogger.initializeTelegramWebhook("abc;123,def;234")
    val logger = GangLogger()
    logger.loggers.head.asInstanceOf[TelegramWebhookLogger].telegramRobotsToSend should contain theSameElementsAs Seq(TelegramRobot(Some("abc"), Some("123")), TelegramRobot(Some("def"), Some("234")))
  }

  it should "initializeTelegramWebhook(robotsChatIdsTokens: Array[Array[String]])" in {
    the[IllegalArgumentException] thrownBy {
      TelegramWebhookLogger.initializeTelegramWebhook("")
    } should have message I18N.getRB.getString("telegramWebhookLogger.initializeTelegramWebhook").format("Array(Array())")
    the[IllegalArgumentException] thrownBy {
      TelegramWebhookLogger.initializeTelegramWebhook("123,,abc")
    } should have message I18N.getRB.getString("telegramWebhookLogger.initializeTelegramWebhook").format("Array(Array(123), Array(), Array(abc))")
    the[IllegalArgumentException] thrownBy {
      TelegramWebhookLogger.initializeTelegramWebhook("123,,abc;123;234")
    } should have message I18N.getRB.getString("telegramWebhookLogger.initializeTelegramWebhook").format("Array(Array(123), Array(), Array(abc, 123, 234))")
    the[IllegalArgumentException] thrownBy {
      TelegramWebhookLogger.initializeTelegramWebhook(null: Array[Array[String]])
    } should have message I18N.getRB.getString("telegramWebhookLogger.initializeTelegramWebhook").format("null")
    the[IllegalArgumentException] thrownBy {
      TelegramWebhookLogger.initializeTelegramWebhook(Array.empty[Array[String]])
    } should have message I18N.getRB.getString("telegramWebhookLogger.initializeTelegramWebhook").format("Array()")
  }

  "telegram webhook logger" should "send a log into telegram with correct chat_id and token" in {
    //TelegramWebhookLogger.setProxy("127.0.0.1", 6080)
    TelegramWebhookLogger.initializeTelegramWebhook("-541655508;1957795670:AAE8KlT0LFdbvgiG1TJlR2kPUKVXLrenDT8")
    val logger = GangLogger()
    retry(2)(logger.info("telegram webhook logger send a log into telegram with correct chat_id and token")) match {
      case Failure(e) => the[SocketTimeoutException] thrownBy (throw e) should have message "connect timed out"
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into telegram with incorrect chat_id and token" in {
    TelegramWebhookLogger.initializeTelegramWebhook("123123;1515:a3af")
    val logger = GangLogger()
    retry(2)(logger.info("telegram webhook logger not send a log into telegram with incorrect key")) match {
      case Failure(e) => the[SocketTimeoutException] thrownBy (throw e) should ((have message "connect timed out") or (have message "Read timed out"))
      case Success(v) => v shouldBe false
    }
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if robotsToSend is empty" in {
    val logger = GangLogger()
    an[IllegalArgumentException] should be thrownBy logger.info("")
  }

  "TelegramWebhookLogger" should "be newed with an IllegalArgumentException thrown if loggerConfig was not set" in {
    the[IllegalArgumentException] thrownBy new TelegramWebhookLogger() should have message "TelegramWebhookLogger.loggerConfig is None"
  }

  it should "be applied with an IllegalArgumentException thrown if initializeConfiguration(c: LoggerConfiguration) or apply(c: LoggerConfiguration) was not set" in {
    the[IllegalArgumentException] thrownBy TelegramWebhookLogger() should have message "You did not pass parameter loggerConfig nor initializeConfiguration"
  }
}
