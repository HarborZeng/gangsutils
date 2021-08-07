package cn.tellyouwhat.gangsutils.common.logger

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WoaWebhookLoggerTest extends AnyFlatSpec with Matchers {

  behavior of "WoaWebhookLoggerTest"

  it should "initializeWoaWebhook(robotsKeys: String)" in {
    a [NullPointerException] should be thrownBy WoaWebhookLogger.initializeWoaWebhook(null: String)
    WoaWebhookLogger.initializeWoaWebhook("abc,def")
    GangLogger().robotsToSend should contain theSameElementsAs Seq("abc", "def")
    WoaWebhookLogger.initializeWoaWebhook("abc")
    GangLogger().robotsToSend should contain theSameElementsAs Seq("abc")
  }

  it should "initializeWoaWebhook(robotsKeys: Array[String])" in {
    a [IllegalArgumentException] should be thrownBy WoaWebhookLogger.initializeWoaWebhook("")
    a [IllegalArgumentException] should be thrownBy WoaWebhookLogger.initializeWoaWebhook("123,,abc")
    a [IllegalArgumentException] should be thrownBy WoaWebhookLogger.initializeWoaWebhook(null: Array[String])
    a [IllegalArgumentException] should be thrownBy WoaWebhookLogger.initializeWoaWebhook(Array.empty[String])
  }

  "woa webhook logger" should "send a log into woa" in {
    WoaWebhookLogger.initializeWoaWebhook("a35a9ed09b9a7bb50dc5cc13c4cc20af")
    val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.WOA_WEBHOOK_LOGGER))
    logger.info("woa webhook logger send a log into woa")
  }

}
