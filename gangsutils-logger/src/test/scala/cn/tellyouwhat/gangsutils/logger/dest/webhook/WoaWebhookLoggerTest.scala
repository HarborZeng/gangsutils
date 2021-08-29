package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.retry
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.WOA_WEBHOOK_LOGGER
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class WoaWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {

  before {
    GangLogger.setLoggerAndConfiguration(Map(
      WOA_WEBHOOK_LOGGER -> LoggerConfiguration()
    ))
  }

  after {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
    WoaWebhookLogger.resetRobotsKeys()
  }

  behavior of "WoaWebhookLoggerTest"

  it should "initializeWoaWebhook(robotsKeys: String)" in {
    the[NullPointerException] thrownBy WoaWebhookLogger.initializeWoaWebhook(null: String) should have message null
    WoaWebhookLogger.initializeWoaWebhook("abc,def")
    val logger1 = GangLogger()
    logger1.loggers.head.asInstanceOf[WoaWebhookLogger].woaRobotsToSend should contain theSameElementsAs Seq("abc", "def")
    WoaWebhookLogger.initializeWoaWebhook("abc")
    val logger2 = GangLogger()
    logger2.loggers.head.asInstanceOf[WoaWebhookLogger].woaRobotsToSend should contain theSameElementsAs Seq("abc")
  }

  it should "initializeWoaWebhook(robotsKeys: Array[String])" in {
    the[IllegalArgumentException] thrownBy {
      WoaWebhookLogger.initializeWoaWebhook("")
    } should have message I18N.getRB.getString("woaWebhookLogger.initializeWoaWebhook").format("Array()")
    the[IllegalArgumentException] thrownBy {
      WoaWebhookLogger.initializeWoaWebhook("123,,abc")
    } should have message I18N.getRB.getString("woaWebhookLogger.initializeWoaWebhook").format("Array(123, , abc)")
    the[IllegalArgumentException] thrownBy {
      WoaWebhookLogger.initializeWoaWebhook(null: Array[String])
    } should have message I18N.getRB.getString("woaWebhookLogger.initializeWoaWebhook").format("null")
    the[IllegalArgumentException] thrownBy {
      WoaWebhookLogger.initializeWoaWebhook(Array.empty[String])
    } should have message I18N.getRB.getString("woaWebhookLogger.initializeWoaWebhook").format("Array()")
  }

  "woa webhook logger" should "send a log into woa with correct key" in {
    WoaWebhookLogger.initializeWoaWebhook("a35a9ed09b9a7bb50dc5cc13c4cc20af")
    val logger = GangLogger()
    retry(2)(logger.info("woa webhook logger send a log into woa with correct key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into woa with incorrect key" in {
    WoaWebhookLogger.initializeWoaWebhook("a3af")
    val logger = GangLogger()
    retry(2)(logger.info("woa webhook logger not send a log into woa with incorrect key")) match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe false
    }
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if robotsToSend is empty" in {
    val logger = GangLogger()
    an[IllegalArgumentException] should be thrownBy logger.info()
  }

}
