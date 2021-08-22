package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.gangfunctions.retry
import cn.tellyouwhat.gangsutils.common.logger.SupportedLogDest.WOA_WEBHOOK_LOGGER
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class WoaWebhookLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter with PrivateMethodTester {

  before {
    GangLogger.resetLoggerConfig()
  }

  after {
    WoaWebhookLogger.resetRobotsKeys()
  }

  behavior of "WoaWebhookLoggerTest"

  it should "initializeWoaWebhook(robotsKeys: String)" in {
    a [NullPointerException] should be thrownBy WoaWebhookLogger.initializeWoaWebhook(null: String)
    WoaWebhookLogger.initializeWoaWebhook("abc,def")
    GangLogger().robotsToSend should contain theSameElementsAs Seq("abc", "def")
    WoaWebhookLogger.initializeWoaWebhook("abc")
    GangLogger().robotsToSend should contain theSameElementsAs Seq("abc")
  }

  it should "initializeWoaWebhook(robotsKeys: Array[String])" in {
    an [IllegalArgumentException] should be thrownBy WoaWebhookLogger.initializeWoaWebhook("")
    an [IllegalArgumentException] should be thrownBy WoaWebhookLogger.initializeWoaWebhook("123,,abc")
    an [IllegalArgumentException] should be thrownBy WoaWebhookLogger.initializeWoaWebhook(null: Array[String])
    an [IllegalArgumentException] should be thrownBy WoaWebhookLogger.initializeWoaWebhook(Array.empty[String])
  }

  "woa webhook logger" should "send a log into woa with correct key" in {
    WoaWebhookLogger.initializeWoaWebhook("a35a9ed09b9a7bb50dc5cc13c4cc20af")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.WOA_WEBHOOK_LOGGER))
    retry(5)(logger.info("woa webhook logger send a log into woa with correct key")) match {
      case Failure(e) => a [SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }
  }

  it should "not send a log into woa with incorrect key" in {
    WoaWebhookLogger.initializeWoaWebhook("a3af")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.WOA_WEBHOOK_LOGGER))
    logger.info("woa webhook logger not send a log into woa with incorrect key") shouldBe false
  }

  "checkPrerequisite" should "throw an IllegalArgumentException if robotsToSend is empty" in {
    val logger = GangLogger(defaultLogDest = WOA_WEBHOOK_LOGGER :: Nil)
    val checkPrerequisite = PrivateMethod[Unit]('checkPrerequisite)
    an [IllegalArgumentException] should be thrownBy { logger invokePrivate checkPrerequisite() }
  }

}
