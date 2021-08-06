package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.gangfunctions.{chainSideEffect, printOrLog}


/**
 * 往 woa 里面发送日志
 */
trait WoaWebhookLogger extends WebhookLogger {

  /**
   * 要发往的机器人的密钥
   */
  protected val robotsToSend: Set[String] = WoaWebhookLogger.robotsToSend.toSet

  /**
   * 执行一条 woa 日志
   *
   * @param msg   日志内容
   * @param level 日志级别
   */
  protected def woaWebhookLog(msg: String, level: LogLevel.Value): Unit = webhookLog(msg, level)

  /**
   * 检查 woa 的 webhook 机器人的密钥是否已经设置
   */
  protected def checkRobotsInitialized(): Unit =
    if (robotsToSend.isEmpty || robotsToSend.exists(_.isEmpty))
      throw new IllegalArgumentException("必须要先调用 WoaWebhookLogger.initializeWoaWebhook 初始化机器人的秘钥才能创建 WoaWebhookLogger 实例")

  protected override def webhookLog(msg: String, level: LogLevel.Value): Unit = {
    robotsToSend.foreach(key =>
      buildLogContent(msg, level) |! (content => sendRequest(
        s"https://woa.wps.cn/api/v1/webhook/send?key=$key",
        body = "{\"msgtype\": \"text\",\"text\": {\"content\": \" " + content + "\"}}"
      ))
    )
  }

  override protected def doTheLogAction(msg: String, level: LogLevel.Value): Unit = {
    checkRobotsInitialized()
    woaWebhookLog(msg, level)
  }
}

import scala.io.AnsiColor.{YELLOW, RESET}

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

  /**
   * 初始化 woa webhook 的密钥
   *
   * @param robotsKeys 密钥，如果是多个，中间用逗号隔开
   */
  def initializeWoaWebhook(robotsKeys: String): Unit = {
    if (robotsKeys == null)
      initializeWoaWebhook(robotsKeys)
    robotsKeys.split(",").map(_.trim) |! initializeWoaWebhook
  }


  /**
   * 初始化 woa webhook 的密钥
   *
   * @param robotsKeys 密钥数组
   */
  def initializeWoaWebhook(robotsKeys: Array[String]): Unit = robotsToSend = {
    if ((robotsKeys != null && robotsKeys.isEmpty) || robotsKeys == null || robotsKeys.exists(_.isEmpty)) {
      throw new IllegalArgumentException(s"initializeWoaWebhook 初始化，但 robotsKeys 传入了: ${robotsKeys.mkString("Array(", ", ", ")")}")
    }
    robotsKeys
  }

}