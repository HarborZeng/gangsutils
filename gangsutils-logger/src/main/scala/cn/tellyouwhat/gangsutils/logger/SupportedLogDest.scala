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
  val PRINTLN_LOGGER: SupportedLogDest.Value = Value(PrintlnLogger.loggerName)

  /**
   * WOA机器人通知的日志枚举
   */
  val WOA_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(WoaWebhookLogger.loggerName)

  /**
   * Slack 通知的日志枚举
   */
  val SLACK_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(SlackWebhookLogger.loggerName)

  /**
   * 企业微信机器人通知的日志枚举
   */
  val QYWX_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(QYWXWebhookLogger.loggerName)

  /**
   * 钉钉机器人通知的日志枚举
   */
  val DINGTALK_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(DingTalkWebhookLogger.loggerName)

  /**
   * ServerChan机器人通知的日志枚举
   */
  val SERVERCHAN_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(ServerChanWebhookLogger.loggerName)

  /**
   * 飞书机器人通知的日志枚举
   */
  val FEISHU_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(FeishuWebhookLogger.loggerName)

  /**
   * telegram 机器人通知的日志枚举
   */
  val TELEGRAM_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(TelegramWebhookLogger.loggerName)

  /**
   * 本地txt文件的日志枚举
   */
  val LOCAL_PLAIN_TEXT_LOGGER: SupportedLogDest.Value = Value(LocalPlainTextLogger.loggerName)

  /**
   * 本地html文件的日志枚举
   */
  val LOCAL_HTML_LOGGER: SupportedLogDest.Value = Value(LocalHtmlLogger.loggerName)

  /**
   * push plus 日志枚举
   */
  val PUSHPULS_WEBHOOK_LOGGER: SupportedLogDest.Value = Value(PushPlusWebhookLogger.loggerName)

}