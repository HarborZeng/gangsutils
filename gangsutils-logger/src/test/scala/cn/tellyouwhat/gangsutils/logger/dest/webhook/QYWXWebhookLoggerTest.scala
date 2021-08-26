package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.retry
import cn.tellyouwhat.gangsutils.logger.{GangLogger, SupportedLogDest}
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.QYWX_WEBHOOK_LOGGER
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class QYWXWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    GangLogger.resetLoggerConfig()
  }

  after {
    QYWXWebhookLogger.resetRobotsKeys()
  }

  behavior of "QYWXWebhookLoggerTest"

  it should "initializeQYWXWebhook(robotsKeys: String)" in {
    a[NullPointerException] should be thrownBy QYWXWebhookLogger.initializeQYWXWebhook(null: String)
    QYWXWebhookLogger.initializeQYWXWebhook("abc,def")
    GangLogger().qywxRobotsToSend should contain theSameElementsAs Seq("abc", "def")
    QYWXWebhookLogger.initializeQYWXWebhook("abc")
    GangLogger().qywxRobotsToSend should contain theSameElementsAs Seq("abc")
  }

  it should "initializeQYWXWebhook(robotsKeys: Array[String])" in {
    an[IllegalArgumentException] should be thrownBy QYWXWebhookLogger.initializeQYWXWebhook("")
    an[IllegalArgumentException] should be thrownBy QYWXWebhookLogger.initializeQYWXWebhook("123,,abc")
    an[IllegalArgumentException] should be thrownBy QYWXWebhookLogger.initializeQYWXWebhook(null: Array[String])
    an[IllegalArgumentException] should be thrownBy QYWXWebhookLogger.initializeQYWXWebhook(Array.empty[String])
  }

  "qywx webhook logger" should "send a log into qywx with correct key" in {
    QYWXWebhookLogger.initializeQYWXWebhook("daf01642-e81a-43a6-a8ec-60967df43578")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.QYWX_WEBHOOK_LOGGER))
    retry(2)(logger.info("qywx webhook logger send a log into qywx with correct key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into qywx with incorrect key" in {
    QYWXWebhookLogger.initializeQYWXWebhook("a3af")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.QYWX_WEBHOOK_LOGGER))
    retry(2)(logger.info("qywx webhook logger not send a log into qywx with incorrect key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe false
    }
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if robotsToSend is empty" in {
    val logger = GangLogger(defaultLogDest = QYWX_WEBHOOK_LOGGER :: Nil)
    an[IllegalArgumentException] should be thrownBy logger.info()
  }

}
