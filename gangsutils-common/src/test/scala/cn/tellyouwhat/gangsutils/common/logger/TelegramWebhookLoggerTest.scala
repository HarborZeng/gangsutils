package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.gangfunctions.retry
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class TelegramWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    GangLogger.resetLoggerConfig()
  }

  after {
    TelegramWebhookLogger.resetRobots()
  }

  behavior of "TelegramWebhookLoggerTest"

  it should "initializeTelegramWebhook(robotsChatIdsTokens: String)" in {
    a [NullPointerException] should be thrownBy TelegramWebhookLogger.initializeTelegramWebhook(null: String)
    an [IllegalArgumentException] should be thrownBy TelegramWebhookLogger.initializeTelegramWebhook("abc,def")
    an [IllegalArgumentException] should be thrownBy TelegramWebhookLogger.initializeTelegramWebhook("abc;123,def")
    an [IllegalArgumentException] should be thrownBy TelegramWebhookLogger.initializeTelegramWebhook("abc")
  }

  it should "initializeTelegramWebhook(robotsChatIdsTokens: Array[Array[String]])" in {
    an [IllegalArgumentException] should be thrownBy TelegramWebhookLogger.initializeTelegramWebhook("")
    an [IllegalArgumentException] should be thrownBy TelegramWebhookLogger.initializeTelegramWebhook("123,,abc")
    an [IllegalArgumentException] should be thrownBy TelegramWebhookLogger.initializeTelegramWebhook("123,,abc;123;234")
    an [IllegalArgumentException] should be thrownBy TelegramWebhookLogger.initializeTelegramWebhook(null: Array[Array[String]])
    an [IllegalArgumentException] should be thrownBy TelegramWebhookLogger.initializeTelegramWebhook(Array.empty[Array[String]])
  }

  "telegram webhook logger" should "send a log into telegram with correct chat_id and token" in {
    TelegramWebhookLogger.initializeTelegramWebhook("-541655508;1957795670:AAE8KlT0LFdbvgiG1TJlR2kPUKVXLrenDT8")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.TELEGRAM_WEBHOOK_LOGGER))
    retry(5)(logger.info("telegram webhook logger send a log into telegram with correct chat_id and token")) match {
      case Failure(e) => a [SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into telegram with incorrect chat_id and token" in {
    TelegramWebhookLogger.initializeTelegramWebhook("123123;1515:a3af")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.TELEGRAM_WEBHOOK_LOGGER))
    logger.info("telegram webhook logger not send a log into telegram with incorrect key") shouldBe false
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if robotsToSend is empty" in {
    val logger = GangLogger(defaultLogDest = SupportedLogDest.TELEGRAM_WEBHOOK_LOGGER :: Nil)
    an [IllegalArgumentException] should be thrownBy { logger.info() }
  }

}
