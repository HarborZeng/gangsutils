package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.retry
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.logger.{GangLogger, SupportedLogDest}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalaj.http.Base64

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
    the[NullPointerException] thrownBy SlackWebhookLogger.initializeSlackUrls(null: String) should have message null
    SlackWebhookLogger.initializeSlackUrls("abc,def")
    GangLogger().slackWebhookURLs should contain theSameElementsAs Seq("abc", "def")
    SlackWebhookLogger.initializeSlackUrls("abc")
    GangLogger().slackWebhookURLs should contain theSameElementsAs Seq("abc")
  }

  it should "initializeSlackUrls(slackUrls: Array[String])" in {
    the[IllegalArgumentException] thrownBy {
      SlackWebhookLogger.initializeSlackUrls("")
    } should have message I18N.getRB.getString("slackWebhookLogger.initializeSlackUrls").format("Array()")
    the[IllegalArgumentException] thrownBy {
      SlackWebhookLogger.initializeSlackUrls("123,,abc")
    } should have message I18N.getRB.getString("slackWebhookLogger.initializeSlackUrls").format("Array(123, , abc)")
    the[IllegalArgumentException] thrownBy {
      SlackWebhookLogger.initializeSlackUrls(null: Array[String])
    } should have message I18N.getRB.getString("slackWebhookLogger.initializeSlackUrls").format("null")
    the[IllegalArgumentException] thrownBy {
      SlackWebhookLogger.initializeSlackUrls(Array.empty[String])
    } should have message I18N.getRB.getString("slackWebhookLogger.initializeSlackUrls").format("Array()")
  }

  "slack webhook logger" should "send a log into slack with correct webhook url" in {
    val slackWebhookURLBase64 = "aHR0cHM6Ly9ob29rcy5zbGFjay5jb20vc2VydmljZXMvVDAyQzNHNVQ4UUwvQjAyQldERjdFNVQvc1dhOHl5N0RkQnlWRWo3TE9nOXdnN3dB"
    val slackWebhookURL = Base64.decodeString(slackWebhookURLBase64)
    SlackWebhookLogger.initializeSlackUrls(slackWebhookURL)
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.SLACK_WEBHOOK_LOGGER))
    retry(2)(logger.info("slack webhook logger send a log into slack with correct url")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into slack with incorrect url" in {
    SlackWebhookLogger.initializeSlackUrls("https://hooks.slack.com/services/T_WRONG/B_WRONG/WRONG")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.SLACK_WEBHOOK_LOGGER))
    retry(2)(logger.info("slack webhook logger not send a log into slack with incorrect url")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe false
    }
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if slackWebhookURLs is empty" in {
    val logger = GangLogger(defaultLogDest = SupportedLogDest.SLACK_WEBHOOK_LOGGER :: Nil)
    an[IllegalArgumentException] should be thrownBy logger.info()
  }

}
