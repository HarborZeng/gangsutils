package cn.tellyouwhat.gangsutils.logger.dest.webhook


import cn.tellyouwhat.gangsutils.core.funcs.{escapeJsonString, stripANSIColor}
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger}

import java.net.URLEncoder

/**
 * A logger that write logs to ServerChan (Server酱)
 */
class ServerChanWebhookLogger extends WebhookLogger {

  override protected val proxyHost: Option[String] = ServerChanWebhookLogger.proxyHost
  override protected val proxyPort: Option[Int] = ServerChanWebhookLogger.proxyPort

  override val loggerConfig: LoggerConfiguration = ServerChanWebhookLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("ServerChanWebhookLogger.loggerConfig is None")
  }

  /**
   * 要发往的机器人的密钥
   */
  val serverChanRobotsToSend: Set[String] = ServerChanWebhookLogger.robotsToSend.toSet

  override protected def webhookLog(msg: String, optionThrowable: Option[Throwable], level: LogLevel.Value): Boolean = {
    val fullLog = buildLog(msg, optionThrowable, level).toString |> stripANSIColor |> escapeJsonString
    serverChanRobotsToSend.map(key =>
      sendRequest(s"https://sctapi.ftqq.com/$key.send",
        form = Seq(
          ("title", URLEncoder.encode(msg, "UTF-8")),
          ("desp", URLEncoder.encode(fullLog, "UTF-8")),
        ))
    ).forall(b => b)
  }

  override protected def checkPrerequisite(): Unit = {
    if (serverChanRobotsToSend.isEmpty || serverChanRobotsToSend.exists(_.isEmpty))
      throw new IllegalArgumentException(I18N.getRB.getString("serverChanWebhookLogger.prerequisite"))
  }
}

/**
 * an object of ServerChanWebhookLogger to set ServerChanWebhookLogger class using
 * <pre>
 * ServerChanWebhookLogger.initializeServerChanWebhook(robotsKeys: String)
 * ServerChanWebhookLogger.initializeServerChanWebhook(robotsKeys: Array[String])
 * ServerChanWebhookLogger.resetRobotsKeys()
 * ServerChanWebhookLogger.initializeConfiguration(c: LoggerConfiguration)
 * ServerChanWebhookLogger.resetConfiguration()
 * </pre>
 */
object ServerChanWebhookLogger extends WebhookLoggerCompanion {

  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.webhook.ServerChanWebhookLogger"

  /**
   * 要发往的机器人的密钥
   */
  private var robotsToSend: Array[String] = Array.empty[String]

  def resetRobotsKeys(): Unit = robotsToSend = Array.empty[String]

  /**
   * 初始化 ServerChan webhook 的密钥
   *
   * @param robotsKeys 密钥，如果是多个，中间用逗号隔开
   */
  def initializeServerChanWebhook(robotsKeys: String): Unit = {
    robotsKeys.split(",").map(_.trim) |! initializeServerChanWebhook
  }

  /**
   * 初始化 ServerChan webhook 的密钥
   *
   * @param robotsKeys 密钥数组
   */
  def initializeServerChanWebhook(robotsKeys: Array[String]): Unit = robotsToSend = {
    if (robotsKeys == null || robotsKeys.isEmpty || robotsKeys.exists(_.isEmpty)) {
      throw new IllegalArgumentException(
        I18N.getRB.getString("serverChanWebhookLogger.initializeServerChanWebhook").format(if (robotsKeys == null) null else robotsKeys.mkString("Array(", ", ", ")")))
    }
    robotsKeys
  }

  override def apply(): Logger = {
    if (loggerConfig.isEmpty)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new ServerChanWebhookLogger()
  }
}
