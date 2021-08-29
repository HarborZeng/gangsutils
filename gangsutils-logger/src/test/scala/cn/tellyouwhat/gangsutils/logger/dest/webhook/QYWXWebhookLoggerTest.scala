package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.retry
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.QYWX_WEBHOOK_LOGGER
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class QYWXWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    GangLogger.setLoggerAndConfiguration(Map(
      QYWX_WEBHOOK_LOGGER -> LoggerConfiguration()
    ))
  }

  after {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
    QYWXWebhookLogger.resetRobotsKeys()
  }

  behavior of "QYWXWebhookLoggerTest"

  it should "initializeQYWXWebhook(robotsKeys: String)" in {
    the[NullPointerException] thrownBy QYWXWebhookLogger.initializeQYWXWebhook(null: String) should have message null
    QYWXWebhookLogger.initializeQYWXWebhook("abc,def")
    val logger1 = GangLogger()
    logger1.loggers.head.asInstanceOf[QYWXWebhookLogger].qywxRobotsToSend should contain theSameElementsAs Seq("abc", "def")
    QYWXWebhookLogger.initializeQYWXWebhook("abc")
    val logger2 = GangLogger()
    logger2.loggers.head.asInstanceOf[QYWXWebhookLogger].qywxRobotsToSend should contain theSameElementsAs Seq("abc")
  }

  it should "initializeQYWXWebhook(robotsKeys: Array[String])" in {
    the[IllegalArgumentException] thrownBy {
      QYWXWebhookLogger.initializeQYWXWebhook("")
    } should have message I18N.getRB.getString("qyexWebhookLogger.initializeQYWXWebhook").format("Array()")
    the[IllegalArgumentException] thrownBy {
      QYWXWebhookLogger.initializeQYWXWebhook("123,,abc")
    } should have message I18N.getRB.getString("qyexWebhookLogger.initializeQYWXWebhook").format("Array(123, , abc)")
    the[IllegalArgumentException] thrownBy {
      QYWXWebhookLogger.initializeQYWXWebhook(null: Array[String])
    } should have message I18N.getRB.getString("qyexWebhookLogger.initializeQYWXWebhook").format("null")
    the[IllegalArgumentException] thrownBy {
      QYWXWebhookLogger.initializeQYWXWebhook(Array.empty[String])
    } should have message I18N.getRB.getString("qyexWebhookLogger.initializeQYWXWebhook").format("Array()")
  }

  "qywx webhook logger" should "send a log into qywx with correct key" in {
    QYWXWebhookLogger.initializeQYWXWebhook("daf01642-e81a-43a6-a8ec-60967df43578")
    val logger = GangLogger()
    retry(2)(logger.info("qywx webhook logger send a log into qywx with correct key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into qywx with incorrect key" in {
    QYWXWebhookLogger.initializeQYWXWebhook("a3af")
    val logger = GangLogger()
    retry(2)(logger.info("qywx webhook logger not send a log into qywx with incorrect key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe false
    }
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if robotsToSend is empty" in {
    val logger = GangLogger()
    an[IllegalArgumentException] should be thrownBy logger.info()
  }

}
