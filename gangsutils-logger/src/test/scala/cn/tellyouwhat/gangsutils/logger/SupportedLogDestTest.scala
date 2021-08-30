package cn.tellyouwhat.gangsutils.logger

import cn.tellyouwhat.gangsutils.logger.dest.PrintlnLogger
import cn.tellyouwhat.gangsutils.logger.dest.webhook._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SupportedLogDestTest extends AnyFlatSpec with Matchers {

  behavior of "SupportedLogDestTest"

  it should "WOA_WEBHOOK_LOGGER" in {
    SupportedLogDest.WOA_WEBHOOK_LOGGER.id shouldEqual 1
    SupportedLogDest.WOA_WEBHOOK_LOGGER.toString shouldEqual WoaWebhookLogger.loggerName
  }

  it should "PRINTLN_LOGGER" in {
    SupportedLogDest.PRINTLN_LOGGER.id shouldEqual 0
    SupportedLogDest.PRINTLN_LOGGER.toString shouldEqual PrintlnLogger.loggerName
  }

  it should "SLACK_WEBHOOK_LOGGER" in {
    SupportedLogDest.SLACK_WEBHOOK_LOGGER.id shouldEqual 2
    SupportedLogDest.SLACK_WEBHOOK_LOGGER.toString shouldEqual SlackWebhookLogger.loggerName
  }


  it should "QYWX_WEBHOOK_LOGGER" in {
    SupportedLogDest.QYWX_WEBHOOK_LOGGER.id shouldEqual 3
    SupportedLogDest.QYWX_WEBHOOK_LOGGER.toString shouldEqual QYWXWebhookLogger.loggerName
  }


  it should "DINGTALK_WEBHOOK_LOGGER" in {
    SupportedLogDest.DINGTALK_WEBHOOK_LOGGER.id shouldEqual 4
    SupportedLogDest.DINGTALK_WEBHOOK_LOGGER.toString shouldEqual DingTalkWebhookLogger.loggerName
  }


  it should "SERVERCHAN_WEBHOOK_LOGGER" in {
    SupportedLogDest.SERVERCHAN_WEBHOOK_LOGGER.id shouldEqual 5
    SupportedLogDest.SERVERCHAN_WEBHOOK_LOGGER.toString shouldEqual ServerChanWebhookLogger.loggerName
  }


  it should "FEISHU_WEBHOOK_LOGGER" in {
    SupportedLogDest.FEISHU_WEBHOOK_LOGGER.id shouldEqual 6
    SupportedLogDest.FEISHU_WEBHOOK_LOGGER.toString shouldEqual FeishuWebhookLogger.loggerName
  }


  it should "TELEGRAM_WEBHOOK_LOGGER" in {
    SupportedLogDest.TELEGRAM_WEBHOOK_LOGGER.id shouldEqual 7
    SupportedLogDest.TELEGRAM_WEBHOOK_LOGGER.toString shouldEqual TelegramWebhookLogger.loggerName
  }

}
