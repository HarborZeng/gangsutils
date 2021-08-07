package cn.tellyouwhat.gangsutils.common.logger

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SupportedLogDestTest extends AnyFlatSpec with Matchers {

  behavior of "SupportedLogDestTest"

  it should "WOA_WEBHOOK_LOGGER" in {
    SupportedLogDest.WOA_WEBHOOK_LOGGER.id shouldEqual 1
    SupportedLogDest.WOA_WEBHOOK_LOGGER.toString shouldEqual WoaWebhookLogger.WOA_WEBHOOK_LOGGER
  }

  it should "PRINTLN_LOGGER" in {
    SupportedLogDest.PRINTLN_LOGGER.id shouldEqual 0
    SupportedLogDest.PRINTLN_LOGGER.toString shouldEqual PrintlnLogger.PRINTLN_LOGGER
  }

}
