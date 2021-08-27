package cn.tellyouwhat.gangsutils.logger

import cn.tellyouwhat.gangsutils.logger.dest.PrintlnLogger
import cn.tellyouwhat.gangsutils.logger.dest.webhook.{DingTalkWebhookLogger, FeishuWebhookLogger, QYWXWebhookLogger, ServerChanWebhookLogger, SlackWebhookLogger, TelegramWebhookLogger, WoaWebhookLogger}
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

  it should "SLACK_WEBHOOK_LOGGER" in {
    SupportedLogDest.SLACK_WEBHOOK_LOGGER.id shouldEqual 2
    SupportedLogDest.SLACK_WEBHOOK_LOGGER.toString shouldEqual SlackWebhookLogger.SLACK_WEBHOOK_LOGGER
  }


  it should "QYWX_WEBHOOK_LOGGER" in {
    SupportedLogDest.QYWX_WEBHOOK_LOGGER.id shouldEqual 3
    SupportedLogDest.QYWX_WEBHOOK_LOGGER.toString shouldEqual QYWXWebhookLogger.QYWX_WEBHOOK_LOGGER
  }


  it should "DINGTALK_WEBHOOK_LOGGER" in {
    SupportedLogDest.DINGTALK_WEBHOOK_LOGGER.id shouldEqual 4
    SupportedLogDest.DINGTALK_WEBHOOK_LOGGER.toString shouldEqual DingTalkWebhookLogger.DINGTALK_WEBHOOK_LOGGER
  }


  it should "SERVERCHAN_WEBHOOK_LOGGER" in {
    SupportedLogDest.SERVERCHAN_WEBHOOK_LOGGER.id shouldEqual 5
    SupportedLogDest.SERVERCHAN_WEBHOOK_LOGGER.toString shouldEqual ServerChanWebhookLogger.SERVERCHAN_WEBHOOK_LOGGER
  }


  it should "FEISHU_WEBHOOK_LOGGER" in {
    SupportedLogDest.FEISHU_WEBHOOK_LOGGER.id shouldEqual 6
    SupportedLogDest.FEISHU_WEBHOOK_LOGGER.toString shouldEqual FeishuWebhookLogger.FEISHU_WEBHOOK_LOGGER
  }


  it should "TELEGRAM_WEBHOOK_LOGGER" in {
    SupportedLogDest.TELEGRAM_WEBHOOK_LOGGER.id shouldEqual 7
    SupportedLogDest.TELEGRAM_WEBHOOK_LOGGER.toString shouldEqual TelegramWebhookLogger.TELEGRAM_WEBHOOK_LOGGER
  }

}
