package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.stripANSIColor
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger}

/**
 * A logger that write logs to Slack
 */
class SlackWebhookLogger extends WebhookLogger {

  override protected val proxyHost: Option[String] = SlackWebhookLogger.proxyHost
  override protected val proxyPort: Option[Int] = SlackWebhookLogger.proxyPort

  override val loggerConfig: LoggerConfiguration = SlackWebhookLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("SlackWebhookLogger.loggerConfig is None")
  }

  /**
   * 要发往的机器人的密钥
   */
  val slackWebhookURLs: Set[String] = SlackWebhookLogger.slackWebhookURLs.toSet

  override protected def webhookLog(msg: String, level: LogLevel.Value): Boolean = {
    val fullLog = buildLog(msg, level).toString |> stripANSIColor
    slackWebhookURLs.map(url => sendRequest(url, body = s"""{"text": "$fullLog"}"""))
      .forall(b => b)
  }

  override protected def checkPrerequisite(): Unit = {
    if (slackWebhookURLs.isEmpty || slackWebhookURLs.exists(_.isEmpty))
      throw new IllegalArgumentException(I18N.getRB.getString("slackWebhookLogger.prerequisite"))
  }

}

/**
 * an object of SlackWebhookLogger to set SlackWebhookLogger class using
 * <pre>
 * SlackWebhookLogger.initializeSlackUrls(slackUrls: String)
 * SlackWebhookLogger.initializeSlackUrls(slackUrls: Array[String])
 * SlackWebhookLogger.resetSlackUrls()
 * SlackWebhookLogger.initializeConfiguration(c: LoggerConfiguration)
 * SlackWebhookLogger.resetConfiguration()
 * </pre>
 */
object SlackWebhookLogger extends WebhookLoggerCompanion {

  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.webhook.SlackWebhookLogger"

  /**
   * 要发往的机器人的密钥
   */
  private var slackWebhookURLs: Seq[String] = Seq.empty[String]

  def resetSlackUrls(): Unit = slackWebhookURLs = Seq.empty[String]

  /**
   * 初始化 Slack urls
   *
   * @param slackUrls webhook url，如果是多个，中间用逗号隔开
   */
  def initializeSlackUrls(slackUrls: String): Unit =
    slackUrls.split(",").map(_.trim) |! initializeSlackUrls

  /**
   * 初始化 Slack urls
   *
   * @param slackUrls webhook url 数组
   */
  def initializeSlackUrls(slackUrls: Array[String]): Unit = slackWebhookURLs = {
    if (slackUrls == null || slackUrls.isEmpty || slackUrls.exists(_.isEmpty)) {
      throw new IllegalArgumentException(
        I18N.getRB.getString("slackWebhookLogger.initializeSlackUrls").format(if (slackUrls == null) null else slackUrls.mkString("Array(", ", ", ")")))
    }
    slackUrls
  }

  override def apply(): Logger = {
    if (loggerConfig.isEmpty)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new SlackWebhookLogger()
  }
}