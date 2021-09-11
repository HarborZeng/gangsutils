package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.{escapeJsonString, stripANSIColor}
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.cc.{LoggerConfiguration, TelegramRobot}
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger}

/**
 * A logger that write logs to telegram (won't work in mainland China)
 */
class TelegramWebhookLogger extends WebhookLogger {

  override protected val proxyHost: Option[String] = TelegramWebhookLogger.proxyHost
  override protected val proxyPort: Option[Int] = TelegramWebhookLogger.proxyPort

  override val loggerConfig: LoggerConfiguration = TelegramWebhookLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("TelegramWebhookLogger.loggerConfig is None")
  }

  /**
   * 要发往的机器人的密钥
   */
  val telegramRobotsToSend: Set[TelegramRobot] = TelegramWebhookLogger.robotsToSend.toSet

  override protected def webhookLog(msg: String, optionThrowable: Option[Throwable], level: LogLevel.Value): Boolean = {
    val fullLog = buildLog(msg, optionThrowable, level).toString |> stripANSIColor |> escapeJsonString
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

/**
 * an object of TelegramWebhookLogger to set TelegramWebhookLogger class using
 * <pre>
 * TelegramWebhookLogger.initializeTelegramWebhook(robotsChatIdsTokens: String)
 * TelegramWebhookLogger.initializeTelegramWebhook(robotsChatIdsTokens: Array[Array[String]])
 * TelegramWebhookLogger.resetRobots()
 * TelegramWebhookLogger.initializeConfiguration(c: LoggerConfiguration)
 * TelegramWebhookLogger.resetConfiguration()
 * </pre>
 */
object TelegramWebhookLogger extends WebhookLoggerCompanion {

  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.webhook.TelegramWebhookLogger"

  /**
   * 要发往的机器人的密钥
   */
  private var robotsToSend: Array[TelegramRobot] = Array.empty[TelegramRobot]

  def resetRobots(): Unit = robotsToSend = Array.empty[TelegramRobot]

  /**
   * 初始化 telegram webhook 的密钥
   *
   * @param robotsChatIdsTokens 密钥和签名，如果是多个，中间用逗号隔开，密钥与签名之间用分号隔开
   */
  def initializeTelegramWebhook(robotsChatIdsTokens: String): Unit = {
    robotsChatIdsTokens.split(",").map(_.trim.split(";").map(_.trim)) |! initializeTelegramWebhook
  }

  /**
   * 初始化 telegram webhook 的密钥
   *
   * @param robotsChatIdsTokens 密钥和签名数组
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

  override def apply(): Logger = {
    if (loggerConfig.isEmpty)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new TelegramWebhookLogger()
  }
}
