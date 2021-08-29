package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.stripANSIColor
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.{LogLevel, LoggerCompanion}
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration

class SlackWebhookLogger extends WebhookLogger {

  override val loggerConfig: LoggerConfiguration = SlackWebhookLogger.loggerConfig

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

object SlackWebhookLogger extends LoggerCompanion {

  val SLACK_WEBHOOK_LOGGER = "cn.tellyouwhat.gangsutils.logger.dest.webhook.SlackWebhookLogger"

  private var slackWebhookURLs: Seq[String] = Seq.empty[String]

  def resetSlackUrls(): Unit = slackWebhookURLs = Seq.empty[String]

  def initializeSlackUrls(slackUrls: String): Unit =
    slackUrls.split(",").map(_.trim) |! initializeSlackUrls


  def initializeSlackUrls(slackUrls: Array[String]): Unit = slackWebhookURLs = {
    if (slackUrls == null || slackUrls.isEmpty || slackUrls.exists(_.isEmpty)) {
      throw new IllegalArgumentException(
        I18N.getRB.getString("slackWebhookLogger.initializeSlackUrls").format(if (slackUrls == null) null else slackUrls.mkString("Array(", ", ", ")")))
    }
    slackUrls
  }

  private var loggerConfig: LoggerConfiguration = _

  override def initializeConfiguration(c: LoggerConfiguration): Unit = loggerConfig = c

  override def apply(c: LoggerConfiguration): SlackWebhookLogger = {
    initializeConfiguration(c)
    apply()
  }

  override def apply(): SlackWebhookLogger = {
    if (loggerConfig == null)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new SlackWebhookLogger()
  }
}