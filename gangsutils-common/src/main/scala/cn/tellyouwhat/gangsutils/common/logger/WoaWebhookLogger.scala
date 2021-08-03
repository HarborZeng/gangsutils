package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.gangfunctions.chainSideEffect


trait WoaWebhookLogger extends WebhookLogger {

  protected val robotsToSend: Set[String] = WoaWebhookLogger.robotsToSend.toSet

  protected def woaWebhookLog(msg: String, level: LogLevel.Value): Unit = webhookLog(msg, level)

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

object WoaWebhookLogger {

  val WOA_WEBHOOK_LOGGER = "woa_webhook_logger"

  private var robotsToSend: Array[String] = Array.empty[String]

  def initializeWoaWebhook(robotsKeys: String): Unit =
    robotsKeys.split(",").map(_.trim) |! initializeWoaWebhook


  def initializeWoaWebhook(robotsKeys: Array[String]): Unit = robotsToSend = {
    if (robotsKeys != null && robotsKeys.isEmpty) {
      println(s"$YELLOW${new IllegalArgumentException(s"【警告】 initializeWoaWebhook 初始化，但 robotsKeys 传入了: ${robotsKeys.mkString("Array(", ", ", ")")}")}$RESET")
    }
    robotsKeys
  }

}