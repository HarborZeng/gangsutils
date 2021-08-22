package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.helper.chaining.{PipeIt, TapIt}

trait SlackWebhookLogger extends WebhookLogger {

  val slackWebhookURLs: Set[String] = SlackWebhookLogger.slackWebhookURLs.toSet

  override protected def webhookLog(msg: String, level: LogLevel.Value): Boolean = {
    slackWebhookURLs.map(url =>
      buildLogContent(msg, level) |> (content => sendRequest(url, body = s"""{"text": "$content"}"""))
    ).forall(b => b)
  }

  override protected def checkPrerequisite(): Unit = {
    if (slackWebhookURLs.isEmpty || slackWebhookURLs.exists(_.isEmpty))
      throw new IllegalArgumentException("必须要先调用 SlackWebhookLogger.initializeSlackUrls 初始化 Slack Webhook URL 才能创建 SlackWebhookLogger 实例")
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
      throw new IllegalArgumentException(s"initializeSlackUrls 初始化，但 slackUrls 传入了: ${if (slackUrls == null) null else slackUrls.mkString("Array(", ", ", ")")}")
    }
    slackUrls
  }

}