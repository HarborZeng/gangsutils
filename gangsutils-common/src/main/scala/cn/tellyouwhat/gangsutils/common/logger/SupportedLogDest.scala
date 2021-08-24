package cn.tellyouwhat.gangsutils.common.logger

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
}