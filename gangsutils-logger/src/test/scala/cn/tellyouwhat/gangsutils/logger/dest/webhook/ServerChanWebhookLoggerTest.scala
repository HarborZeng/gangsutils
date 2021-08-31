package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.retry
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.SERVERCHAN_WEBHOOK_LOGGER
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class ServerChanWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    GangLogger.setLoggerAndConfiguration(Map(
      SERVERCHAN_WEBHOOK_LOGGER -> LoggerConfiguration(isDTEnabled = false, isHostnameEnabled = false)
    ))
  }

  after {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
    ServerChanWebhookLogger.resetRobotsKeys()
    ServerChanWebhookLogger.resetConfiguration()
  }

  behavior of "ServerChanWebhookLoggerTest"

  it should "initializeServerChanWebhook(robotsKeys: String)" in {
    the[NullPointerException] thrownBy ServerChanWebhookLogger.initializeServerChanWebhook(null: String) should have message null
    ServerChanWebhookLogger.initializeServerChanWebhook("abc,def")
    val logger1 = GangLogger()
    logger1.loggers.head.asInstanceOf[ServerChanWebhookLogger].serverChanRobotsToSend should contain theSameElementsAs Seq("abc", "def")
    ServerChanWebhookLogger.initializeServerChanWebhook("abc")
    val logger2 = GangLogger()
    logger2.loggers.head.asInstanceOf[ServerChanWebhookLogger].serverChanRobotsToSend should contain theSameElementsAs Seq("abc")
  }

  it should "initializeServerChanWebhook(robotsKeys: Array[String])" in {
    the[IllegalArgumentException] thrownBy {
      ServerChanWebhookLogger.initializeServerChanWebhook("")
    } should have message I18N.getRB.getString("serverChanWebhookLogger.initializeServerChanWebhook").format("Array()")
    the[IllegalArgumentException] thrownBy {
      ServerChanWebhookLogger.initializeServerChanWebhook("123,,abc")
    } should have message I18N.getRB.getString("serverChanWebhookLogger.initializeServerChanWebhook").format("Array(123, , abc)")
    the[IllegalArgumentException] thrownBy {
      ServerChanWebhookLogger.initializeServerChanWebhook(null: Array[String])
    } should have message I18N.getRB.getString("serverChanWebhookLogger.initializeServerChanWebhook").format("null")
    the[IllegalArgumentException] thrownBy {
      ServerChanWebhookLogger.initializeServerChanWebhook(Array.empty[String])
    } should have message I18N.getRB.getString("serverChanWebhookLogger.initializeServerChanWebhook").format("Array()")

  }

  "serverChan webhook logger" should "send a log into serverChan with correct key" in {
    ServerChanWebhookLogger.initializeServerChanWebhook("SCT67129TLSijZn947Hz0s3FtPx6rANpS")
    val logger = GangLogger()
    retry(2)(logger.info("serverChan webhook logger send a log into serverChan with correct key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into serverChan with incorrect key" in {
    ServerChanWebhookLogger.initializeServerChanWebhook("a3af")
    val logger = GangLogger()
    retry(2)(logger.info("serverChan webhook logger not send a log into serverChan with incorrect key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe false
    }
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if robotsToSend is empty" in {
    val logger = GangLogger()
    an[IllegalArgumentException] should be thrownBy logger.info()
  }

  "ServerChanWebhookLogger" should "be newed with an IllegalArgumentException thrown if loggerConfig was not set" in {
    the[IllegalArgumentException] thrownBy new ServerChanWebhookLogger() should have message "ServerChanWebhookLogger.loggerConfig is None"
  }

  it should "be applied with an IllegalArgumentException thrown if initializeConfiguration(c: LoggerConfiguration) or apply(c: LoggerConfiguration) was not set" in {
    the[IllegalArgumentException] thrownBy ServerChanWebhookLogger() should have message "You did not pass parameter loggerConfig nor initializeConfiguration"
  }

}
