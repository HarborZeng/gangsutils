package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.gangfunctions.stripANSIColor
import cn.tellyouwhat.gangsutils.common.helper.I18N
import cn.tellyouwhat.gangsutils.common.helper.chaining.{PipeIt, TapIt}

import java.net.URLEncoder

trait ServerChanWebhookLogger extends WebhookLogger {
  /**
   * 要发往的机器人的密钥
   */
  val serverChanRobotsToSend: Set[String] = ServerChanWebhookLogger.robotsToSend.toSet

  override protected def webhookLog(msg: String, level: LogLevel.Value): Boolean = {
    val fullLog = buildLog(msg, level).toString |> stripANSIColor
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

object ServerChanWebhookLogger {

  /**
   * SERVERCHAN_WEBHOOK_LOGGER 文本
   */
  val SERVERCHAN_WEBHOOK_LOGGER = "serverchan_webhook_logger"

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

}
