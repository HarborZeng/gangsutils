package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.retry
import cn.tellyouwhat.gangsutils.logger.{GangLogger, SupportedLogDest}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class ServerChanWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    GangLogger.resetLoggerConfig()
  }

  after {
    ServerChanWebhookLogger.resetRobotsKeys()
  }

  behavior of "ServerChanWebhookLoggerTest"

  it should "initializeServerChanWebhook(robotsKeys: String)" in {
    a[NullPointerException] should be thrownBy ServerChanWebhookLogger.initializeServerChanWebhook(null: String)
    ServerChanWebhookLogger.initializeServerChanWebhook("abc,def")
    GangLogger().serverChanRobotsToSend should contain theSameElementsAs Seq("abc", "def")
    ServerChanWebhookLogger.initializeServerChanWebhook("abc")
    GangLogger().serverChanRobotsToSend should contain theSameElementsAs Seq("abc")
  }

  it should "initializeServerChanWebhook(robotsKeys: Array[String])" in {
    an[IllegalArgumentException] should be thrownBy ServerChanWebhookLogger.initializeServerChanWebhook("")
    an[IllegalArgumentException] should be thrownBy ServerChanWebhookLogger.initializeServerChanWebhook("123,,abc")
    an[IllegalArgumentException] should be thrownBy ServerChanWebhookLogger.initializeServerChanWebhook(null: Array[String])
    an[IllegalArgumentException] should be thrownBy ServerChanWebhookLogger.initializeServerChanWebhook(Array.empty[String])
  }

  "serverChan webhook logger" should "send a log into serverChan with correct key" in {
    ServerChanWebhookLogger.initializeServerChanWebhook("SCT67129TLSijZn947Hz0s3FtPx6rANpS")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.SERVERCHAN_WEBHOOK_LOGGER))
    retry(2)(logger.info("serverChan webhook logger send a log into serverChan with correct key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into serverChan with incorrect key" in {
    ServerChanWebhookLogger.initializeServerChanWebhook("a3af")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.SERVERCHAN_WEBHOOK_LOGGER))
    retry(2)(logger.info("serverChan webhook logger not send a log into serverChan with incorrect key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe false
    }
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if robotsToSend is empty" in {
    val logger = GangLogger(defaultLogDest = SupportedLogDest.SERVERCHAN_WEBHOOK_LOGGER :: Nil)
    an[IllegalArgumentException] should be thrownBy logger.info()
  }

}
