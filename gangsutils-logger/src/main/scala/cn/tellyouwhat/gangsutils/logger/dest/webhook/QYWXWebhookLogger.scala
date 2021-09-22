package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.{escapeJsonString, stripANSIColor}
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger}

import java.util.Objects

/**
 * 往企业微信里面发送日志
 */
class QYWXWebhookLogger extends WebhookLogger {

  override protected val proxyHost: Option[String] = QYWXWebhookLogger.proxyHost
  override protected val proxyPort: Option[Int] = QYWXWebhookLogger.proxyPort

  override val loggerConfig: LoggerConfiguration = QYWXWebhookLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("QYWXWebhookLogger.loggerConfig is None")
  }

  /**
   * 要发往的机器人的密钥
   */
  val qywxRobotsToSend: Set[String] = QYWXWebhookLogger.robotsToSend.toSet

  override protected def webhookLog(msg: String, optionThrowable: Option[Throwable], level: LogLevel.Value): Boolean = {
    val fullLog = buildLog(msg, optionThrowable, level).toString |> stripANSIColor |> escapeJsonString
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
 * an object of QYWXWebhookLogger to set QYWXWebhookLogger class using
 * <pre>
 * QYWXWebhookLogger.initializeQYWXWebhook(robotsKeys: String)
 * QYWXWebhookLogger.initializeQYWXWebhook(robotsKeys: Array[String])
 * QYWXWebhookLogger.resetRobotsKeys()
 * QYWXWebhookLogger.initializeConfiguration(c: LoggerConfiguration)
 * QYWXWebhookLogger.resetConfiguration()
 * </pre>
 */
object QYWXWebhookLogger extends WebhookLoggerCompanion {

  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.webhook.QYWXWebhookLogger"

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
    Objects.requireNonNull(robotsKeys)
    if (robotsKeys.isEmpty || robotsKeys.exists(_.isEmpty)) {
      throw new IllegalArgumentException(
        I18N.getRB.getString("qyexWebhookLogger.initializeQYWXWebhook").format(robotsKeys.mkString("Array(", ", ", ")")))
    }
    robotsKeys
  }

  override def apply(): Logger = {
    if (loggerConfig.isEmpty)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new QYWXWebhookLogger()
  }
}