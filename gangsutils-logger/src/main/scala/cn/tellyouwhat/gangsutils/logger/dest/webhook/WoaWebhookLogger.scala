package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.stripANSIColor
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger, LoggerCompanion}

/**
 * 往 woa 里面发送日志
 */
class WoaWebhookLogger extends WebhookLogger {

  override val loggerConfig: LoggerConfiguration = WoaWebhookLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("WoaWebhookLogger.loggerConfig is None")
  }


  /**
   * 要发往的机器人的密钥
   */
  val woaRobotsToSend: Set[String] = WoaWebhookLogger.robotsToSend.toSet

  override protected def webhookLog(msg: String, level: LogLevel.Value): Boolean = {
    val fullLog = buildLog(msg, level).toString |> stripANSIColor
    woaRobotsToSend.map(key =>
      sendRequest(s"https://woa.wps.cn/api/v1/webhook/send?key=$key",
        body = s"""{"msgtype": "text","text": {"content": "$fullLog"}}""")
    ).forall(b => b)
  }

  override protected def checkPrerequisite(): Unit = {
    if (woaRobotsToSend.isEmpty || woaRobotsToSend.exists(_.isEmpty))
      throw new IllegalArgumentException(I18N.getRB.getString("woaWebhookLogger.prerequisite"))
  }
}

/**
 * an object of WoaWebhookLogger to set WoaWebhookLogger class using
 * <pre>
 * WoaWebhookLogger.initializeWoaWebhook(robotsKeys: String)
 * WoaWebhookLogger.initializeWoaWebhook(robotsKeys: Array[String])
 * WoaWebhookLogger.resetRobotsKeys()
 * WoaWebhookLogger.initializeConfiguration(c: LoggerConfiguration)
 * WoaWebhookLogger.resetConfiguration()
 * </pre>
 */
object WoaWebhookLogger extends LoggerCompanion {

  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.webhook.WoaWebhookLogger"

  /**
   * 要发往的机器人的密钥
   */
  private var robotsToSend: Array[String] = Array.empty[String]

  def resetRobotsKeys(): Unit = robotsToSend = Array.empty[String]

  /**
   * 初始化 woa webhook 的密钥
   *
   * @param robotsKeys 密钥，如果是多个，中间用逗号隔开
   */
  def initializeWoaWebhook(robotsKeys: String): Unit = {
    robotsKeys.split(",").map(_.trim) |! initializeWoaWebhook
  }

  /**
   * 初始化 woa webhook 的密钥
   *
   * @param robotsKeys 密钥数组
   */
  def initializeWoaWebhook(robotsKeys: Array[String]): Unit = robotsToSend = {
    if (robotsKeys == null || robotsKeys.isEmpty || robotsKeys.exists(_.isEmpty)) {
      throw new IllegalArgumentException(
        I18N.getRB.getString("woaWebhookLogger.initializeWoaWebhook").format(if (robotsKeys == null) null else robotsKeys.mkString("Array(", ", ", ")")))
    }
    robotsKeys
  }

  override def apply(): Logger = {
    if (loggerConfig.isEmpty)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new WoaWebhookLogger()
  }
}