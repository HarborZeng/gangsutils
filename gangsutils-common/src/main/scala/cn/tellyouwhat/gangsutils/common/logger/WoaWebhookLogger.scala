package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.gangfunctions.stripANSIColor
import cn.tellyouwhat.gangsutils.common.helper.I18N
import cn.tellyouwhat.gangsutils.common.helper.chaining.{PipeIt, TapIt}


/**
 * 往 woa 里面发送日志
 */
trait WoaWebhookLogger extends WebhookLogger {

  /**
   * 要发往的机器人的密钥
   */
  val woaRobotsToSend: Set[String] = WoaWebhookLogger.robotsToSend.toSet

  override protected def webhookLog(msg: String, level: LogLevel.Value): Boolean = {
    val content = buildLogContent(msg)
    val fullLog = addLeadingHead(content, level) |> stripANSIColor
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
 * woa webhook 日志的伴生对象
 */
object WoaWebhookLogger {

  /**
   * WOA_WEBHOOK_LOGGER 文本
   */
  val WOA_WEBHOOK_LOGGER = "woa_webhook_logger"

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

}