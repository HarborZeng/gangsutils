package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.retry
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.SLACK_WEBHOOK_LOGGER
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalaj.http.Base64

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class SlackWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    GangLogger.setLoggerAndConfiguration(Map(
      SLACK_WEBHOOK_LOGGER -> LoggerConfiguration()
    ))
  }

  after {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
    SlackWebhookLogger.resetSlackUrls()
    SlackWebhookLogger.resetConfiguration()
  }

  behavior of "SlackWebhookLoggerTest"

  it should "initializeSlackUrls(slackUrls: String)" in {
    the[NullPointerException] thrownBy SlackWebhookLogger.initializeSlackUrls(null: String) should have message null
    SlackWebhookLogger.initializeSlackUrls("abc,def")
    val logger1 = GangLogger()
    logger1.loggers.head.asInstanceOf[SlackWebhookLogger].slackWebhookURLs should contain theSameElementsAs Seq("abc", "def")
    SlackWebhookLogger.initializeSlackUrls("abc")
    val logger2 = GangLogger()
    logger2.loggers.head.asInstanceOf[SlackWebhookLogger].slackWebhookURLs should contain theSameElementsAs Seq("abc")
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
    val logger = GangLogger()
    retry(2)(logger.info("slack webhook logger send a log into slack with correct url")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into slack with incorrect url" in {
    SlackWebhookLogger.initializeSlackUrls("https://hooks.slack.com/services/T_WRONG/B_WRONG/WRONG")
    val logger = GangLogger()
    retry(2)(logger.info("slack webhook logger not send a log into slack with incorrect url")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe false
    }
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if slackWebhookURLs is empty" in {
    val logger = GangLogger()
    an[IllegalArgumentException] should be thrownBy logger.info()
  }

  "SlackWebhookLogger" should "be newed with an IllegalArgumentException thrown if loggerConfig was not set" in {
    the [IllegalArgumentException] thrownBy new SlackWebhookLogger() should have message "SlackWebhookLogger.loggerConfig is None"
  }

  it should "be applied with an IllegalArgumentException thrown if initializeConfiguration(c: LoggerConfiguration) or apply(c: LoggerConfiguration) was not set" in {
    the [IllegalArgumentException] thrownBy SlackWebhookLogger() should have message "You did not pass parameter loggerConfig nor initializeConfiguration"
  }
}
