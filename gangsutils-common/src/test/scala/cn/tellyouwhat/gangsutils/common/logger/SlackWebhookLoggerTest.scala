package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.gangfunctions.retry
import cn.tellyouwhat.gangsutils.common.logger.SupportedLogDest.SLACK_WEBHOOK_LOGGER
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfter

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class SlackWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    GangLogger.resetLoggerConfig()
  }

  after {
    SlackWebhookLogger.resetSlackUrls()
  }

  behavior of "SlackWebhookLoggerTest"

  it should "initializeSlackUrls(slackUrls: String)" in {
    a [NullPointerException] should be thrownBy SlackWebhookLogger.initializeSlackUrls(null: String)
    SlackWebhookLogger.initializeSlackUrls("abc,def")
    GangLogger().slackWebhookURLs should contain theSameElementsAs Seq("abc", "def")
    SlackWebhookLogger.initializeSlackUrls("abc")
    GangLogger().slackWebhookURLs should contain theSameElementsAs Seq("abc")
  }

  it should "initializeSlackUrls(slackUrls: Array[String])" in {
    an [IllegalArgumentException] should be thrownBy SlackWebhookLogger.initializeSlackUrls("")
    an [IllegalArgumentException] should be thrownBy SlackWebhookLogger.initializeSlackUrls("123,,abc")
    an [IllegalArgumentException] should be thrownBy SlackWebhookLogger.initializeSlackUrls(null: Array[String])
    an [IllegalArgumentException] should be thrownBy SlackWebhookLogger.initializeSlackUrls(Array.empty[String])
  }

  "slack webhook logger" should "send a log into slack with correct webhook url" in {
    SlackWebhookLogger.initializeSlackUrls("https://hooks.slack.com/services/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")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.SLACK_WEBHOOK_LOGGER))
    retry(5)(logger.info("slack webhook logger send a log into slack with correct url")) match {
      case Failure(e) => a [SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into slack with incorrect url" in {
    SlackWebhookLogger.initializeSlackUrls("https://hooks.slack.com/services/T_WRONG/B_WRONG/WRONG")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.SLACK_WEBHOOK_LOGGER))
    logger.info("slack webhook logger not send a log into slack with incorrect url") shouldBe false
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if slackWebhookURLs is empty" in {
    val logger = GangLogger(defaultLogDest = SLACK_WEBHOOK_LOGGER :: Nil)
    an [IllegalArgumentException] should be thrownBy { logger.info() }
  }

}