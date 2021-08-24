package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.helper.chaining.TapIt
import cn.tellyouwhat.gangsutils.common.helper.I18N

case class TelegramRobot(chatID: Option[String], token: Option[String])

trait TelegramWebhookLogger extends WebhookLogger {
  
  /**
   * 要发往的机器人的密钥
   */
  val telegramRobotsToSend: Set[TelegramRobot] = TelegramWebhookLogger.robotsToSend.toSet
  
  override protected def webhookLog(msg: String, level: LogLevel.Value): Boolean = {
    val content = buildLogContent(msg)
    val fullLog = addLeadingHead(content, level).replaceAll("""\e\[[\d;]*[^\d;]""", "")
    telegramRobotsToSend.map(robot => {
      val targetURL = s"https://api.telegram.org/bot${robot.token.get}/sendMessage"
      sendRequest(targetURL, body = s"""{"chat_id": "${robot.chatID.get}", "text": "$fullLog"}""")
    }).forall(b => b)
  }

  override protected def checkPrerequisite(): Unit = {
    if (telegramRobotsToSend.isEmpty)
      throw new IllegalArgumentException(I18N.getRB.getString("telegramWebhookLogger.prerequisite"))
  }
}

object TelegramWebhookLogger {

  /**
   * TELEGRAM_WEBHOOK_LOGGER 文本
   */
  val TELEGRAM_WEBHOOK_LOGGER = "telegram_webhook_logger"

  /**
   * 要发往的机器人的密钥
   */
  private var robotsToSend: Array[TelegramRobot] = Array.empty[TelegramRobot]

  def resetRobots(): Unit = robotsToSend = Array.empty[TelegramRobot]

  /**
   * 初始化 telegram webhook 的密钥
   *
   * @param robotsChatIdsTokens 密钥，如果是多个，中间用逗号隔开
   */
  def initializeTelegramWebhook(robotsChatIdsTokens: String): Unit = {
    robotsChatIdsTokens.split(",").map(_.trim.split(";").map(_.trim)) |! initializeTelegramWebhook
  }


  /**
   * 初始化 telegram webhook 的密钥
   *
   * @param robotsChatIdsTokens 密钥数组
   */
  def initializeTelegramWebhook(robotsChatIdsTokens: Array[Array[String]]): Unit = robotsToSend = {
    if ((robotsChatIdsTokens != null && robotsChatIdsTokens.isEmpty) ||
      robotsChatIdsTokens == null ||
      robotsChatIdsTokens.exists(_.isEmpty) ||
      robotsChatIdsTokens.exists(_.exists(_.isEmpty)) ||
      robotsChatIdsTokens.exists(p => p.length != 2)
    ) {
      throw new IllegalArgumentException(
        I18N.getRB.getString("telegramWebhookLogger.initializeTelegramWebhook").format(if (robotsChatIdsTokens == null) null else robotsChatIdsTokens.mkString("Array(", ", ", ")")))
    }
    robotsChatIdsTokens.map(chatIDToken => {
        val chatID = chatIDToken.head
        val token = chatIDToken.last
        TelegramRobot(Some(chatID), Some(token))
    })
  }
}
