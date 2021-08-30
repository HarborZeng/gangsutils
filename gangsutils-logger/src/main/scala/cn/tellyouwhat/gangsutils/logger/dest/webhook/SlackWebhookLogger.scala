package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.stripANSIColor
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.{LogLevel, LoggerCompanion}

class SlackWebhookLogger extends WebhookLogger {

  override val loggerConfig: LoggerConfiguration = SlackWebhookLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("SlackWebhookLogger.loggerConfig is None")
  }

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

  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.webhook.SlackWebhookLogger"

  override private[logger] var loggerConfig: Option[LoggerConfiguration] = None

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

  override def apply(c: LoggerConfiguration): SlackWebhookLogger = {
    initializeConfiguration(c)
    apply()
  }

  override def initializeConfiguration(c: LoggerConfiguration): Unit = loggerConfig = Some(c)

  override def resetConfiguration(): Unit = loggerConfig = None

  override def apply(): SlackWebhookLogger = {
    if (loggerConfig.isEmpty)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new SlackWebhookLogger()
  }
}