package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.stripANSIColor
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.{LogLevel, LoggerCompanion}
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration

/**
 * 往企业微信里面发送日志
 */
class QYWXWebhookLogger extends WebhookLogger {
  override val loggerConfig: LoggerConfiguration = QYWXWebhookLogger.loggerConfig

  /**
   * 要发往的机器人的密钥
   */
  val qywxRobotsToSend: Set[String] = QYWXWebhookLogger.robotsToSend.toSet

  override protected def webhookLog(msg: String, level: LogLevel.Value): Boolean = {
    val fullLog = buildLog(msg, level).toString |> stripANSIColor
    qywxRobotsToSend.map(key =>
      sendRequest(s"https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=$key",
        body = s"""{"msgtype": "text","text": {"content": "$fullLog"}}""")
    ).forall(b => b)
  }

  override protected def checkPrerequisite(): Unit = {
    if (qywxRobotsToSend.isEmpty || qywxRobotsToSend.exists(_.isEmpty))
      throw new IllegalArgumentException(I18N.getRB.getString("qywxWebhookLogger.prerequisite"))
  }
}


/**
 * QYWX webhook 日志的伴生对象
 */
object QYWXWebhookLogger extends LoggerCompanion {

  /**
   * QYWX_WEBHOOK_LOGGER 文本
   */
  val QYWX_WEBHOOK_LOGGER = "cn.tellyouwhat.gangsutils.logger.dest.webhook.QYWXWebhookLogger"

  /**
   * 要发往的机器人的密钥
   */
  private var robotsToSend: Array[String] = Array.empty[String]

  def resetRobotsKeys(): Unit = robotsToSend = Array.empty[String]

  /**
   * 初始化 QYWX webhook 的密钥
   *
   * @param robotsKeys 密钥，如果是多个，中间用逗号隔开
   */
  def initializeQYWXWebhook(robotsKeys: String): Unit = {
    robotsKeys.split(",").map(_.trim) |! initializeQYWXWebhook
  }


  /**
   * 初始化 QYWX webhook 的密钥
   *
   * @param robotsKeys 密钥数组
   */
  def initializeQYWXWebhook(robotsKeys: Array[String]): Unit = robotsToSend = {
    if (robotsKeys == null || robotsKeys.isEmpty || robotsKeys.exists(_.isEmpty)) {
      throw new IllegalArgumentException(
        I18N.getRB.getString("qyexWebhookLogger.initializeQYWXWebhook").format(if (robotsKeys == null) null else robotsKeys.mkString("Array(", ", ", ")")))
    }
    robotsKeys
  }

  private var loggerConfig: LoggerConfiguration = _

  override def initializeConfiguration(c: LoggerConfiguration): Unit = loggerConfig = c

  override  def apply(c: LoggerConfiguration): QYWXWebhookLogger = {
    initializeConfiguration(c)
    apply()
  }

  override def apply(): QYWXWebhookLogger = {
    if (loggerConfig == null)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new QYWXWebhookLogger()
  }
}