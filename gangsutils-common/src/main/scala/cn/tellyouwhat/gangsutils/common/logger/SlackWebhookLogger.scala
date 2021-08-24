package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.helper.I18N
import cn.tellyouwhat.gangsutils.common.helper.chaining.{PipeIt, TapIt}

trait SlackWebhookLogger extends WebhookLogger {

  val slackWebhookURLs: Set[String] = SlackWebhookLogger.slackWebhookURLs.toSet

  override protected def webhookLog(msg: String, level: LogLevel.Value): Boolean = {
    val content = buildLogContent(msg)
    val fullLog = addLeadingHead(content, level).replaceAll("""\e\[[\d;]*[^\d;]""", "")
    slackWebhookURLs.map(url => sendRequest(url, body = s"""{"text": "$fullLog"}"""))
      .forall(b => b)
  }

  override protected def checkPrerequisite(): Unit = {
    if (slackWebhookURLs.isEmpty || slackWebhookURLs.exists(_.isEmpty))
      throw new IllegalArgumentException(I18N.getRB.getString("slackWebhookLogger.prerequisite"))
  }

}

object SlackWebhookLogger {

  val SLACK_WEBHOOK_LOGGER = "slack_webhook_logger"

  private var slackWebhookURLs: Seq[String] = Seq.empty[String]

  def resetSlackUrls(): Unit = slackWebhookURLs = Seq.empty[String]

  def initializeSlackUrls(slackUrls: String): Unit =
    slackUrls.split(",").map(_.trim) |! initializeSlackUrls


  def initializeSlackUrls(slackUrls: Array[String]): Unit = slackWebhookURLs = {
    if ((slackUrls != null && slackUrls.isEmpty) || slackUrls == null || slackUrls.exists(_.isEmpty)) {
      throw new IllegalArgumentException(
        I18N.getRB.getString("slackWebhookLogger.initializeSlackUrls").format(if (slackUrls == null) null else slackUrls.mkString("Array(", ", ", ")")))
    }
    slackUrls
  }

}