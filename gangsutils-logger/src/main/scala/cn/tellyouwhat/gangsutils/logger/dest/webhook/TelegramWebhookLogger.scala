package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.stripANSIColor
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.cc.{LoggerConfiguration, TelegramRobot}
import cn.tellyouwhat.gangsutils.logger.{LogLevel, LoggerCompanion}

class TelegramWebhookLogger extends WebhookLogger {

  override val loggerConfig: LoggerConfiguration = TelegramWebhookLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("TelegramWebhookLogger.loggerConfig is None")
  }

  /**
   * 要发往的机器人的密钥
   */
  val telegramRobotsToSend: Set[TelegramRobot] = TelegramWebhookLogger.robotsToSend.toSet

  override protected def webhookLog(msg: String, level: LogLevel.Value): Boolean = {
    val fullLog = buildLog(msg, level).toString |> stripANSIColor
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

object TelegramWebhookLogger extends LoggerCompanion {

  /**
   * TELEGRAM_WEBHOOK_LOGGER 文本
   */
  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.webhook.TelegramWebhookLogger"

  override private[logger] var loggerConfig: Option[LoggerConfiguration] = None
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
    if (robotsChatIdsTokens == null ||
      robotsChatIdsTokens.isEmpty ||
      robotsChatIdsTokens.exists(_.isEmpty) ||
      robotsChatIdsTokens.exists(_.exists(_.isEmpty)) ||
      robotsChatIdsTokens.exists(p => p.length != 2)
    ) {
      throw new IllegalArgumentException(
        I18N.getRB.getString("telegramWebhookLogger.initializeTelegramWebhook").format(if (robotsChatIdsTokens == null) null else robotsChatIdsTokens.map(_.mkString("Array(", ", ", ")")).mkString("Array(", ", ", ")")))
    }
    robotsChatIdsTokens.map(chatIDToken => {
      val chatID = chatIDToken.head
      val token = chatIDToken.last
      TelegramRobot(Some(chatID), Some(token))
    })
  }

  override def apply(c: LoggerConfiguration): TelegramWebhookLogger = {
    initializeConfiguration(c)
    apply()
  }

  override def initializeConfiguration(c: LoggerConfiguration): Unit = loggerConfig = Some(c)
  override def resetConfiguration(): Unit = loggerConfig = None

  override def apply(): TelegramWebhookLogger = {
    if (loggerConfig.isEmpty)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new TelegramWebhookLogger()
  }
}
