package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.helper.I18N
import cn.tellyouwhat.gangsutils.common.helper.chaining.{PipeIt, TapIt}


/**
 * 往企业微信里面发送日志
 */
trait QYWXWebhookLogger extends WebhookLogger {

  /**
   * 要发往的机器人的密钥
   */
  val qywxRobotsToSend: Set[String] = QYWXWebhookLogger.robotsToSend.toSet

  override protected def webhookLog(msg: String, level: LogLevel.Value): Boolean = {
    val content = buildLogContent(msg)
    val fullLog = addLeadingHead(content, level).replaceAll("""\e\[[\d;]*[^\d;]""", "")
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
object QYWXWebhookLogger {

  /**
   * QYWX_WEBHOOK_LOGGER 文本
   */
  val QYWX_WEBHOOK_LOGGER = "qywx_webhook_logger"

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

}