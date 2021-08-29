package cn.tellyouwhat.gangsutils.logger

import cn.tellyouwhat.gangsutils.logger.dest.PrintlnLogger
import cn.tellyouwhat.gangsutils.logger.dest.fs.{LocalHtmlLogger, LocalPlainTextLogger}
import cn.tellyouwhat.gangsutils.logger.dest.webhook._

/**
 * Supported Log Destination
 */
object SupportedLogDest extends Enumeration {
  /**
   * 标准输出的打印日志枚举
   */
  val PRINTLN_LOGGER: SupportedLogDest.Value = Value(PrintlnLogger.PRINTLN_LOGGER)

  /**
   * WOA机器人通知的日志枚举
   */
  val WOA_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(WoaWebhookLogger.WOA_WEBHOOK_LOGGER)

  /**
   * Slack 通知的日志枚举
   */
  val SLACK_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(SlackWebhookLogger.SLACK_WEBHOOK_LOGGER)

  /**
   * 企业微信机器人通知的日志枚举
   */
  val QYWX_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(QYWXWebhookLogger.QYWX_WEBHOOK_LOGGER)

  /**
   * 钉钉机器人通知的日志枚举
   */
  val DINGTALK_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(DingTalkWebhookLogger.DINGTALK_WEBHOOK_LOGGER)

  /**
   * ServerChan机器人通知的日志枚举
   */
  val SERVERCHAN_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(ServerChanWebhookLogger.SERVERCHAN_WEBHOOK_LOGGER)

  /**
   * 飞书机器人通知的日志枚举
   */
  val FEISHU_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(FeishuWebhookLogger.FEISHU_WEBHOOK_LOGGER)

  /**
   * telegram 机器人通知的日志枚举
   */
  val TELEGRAM_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(TelegramWebhookLogger.TELEGRAM_WEBHOOK_LOGGER)

  /**
   * 本地txt文件的日志枚举
   */
  val LOCAL_PLAIN_TEXT_LOGGER: SupportedLogDest.Value = Value(LocalPlainTextLogger.LOCAL_PLAIN_TEXT_LOGGER)

  /**
   * 本地html文件的日志枚举
   */
  val LOCAL_HTML_LOGGER: SupportedLogDest.Value = Value(LocalHtmlLogger.LOCAL_HTML_LOGGER)

}