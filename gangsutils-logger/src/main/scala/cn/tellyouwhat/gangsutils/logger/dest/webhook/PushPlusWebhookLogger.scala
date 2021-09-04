package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.{escapeQuotationMark, stripANSIColor}
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger}

import scala.io.Source

/**
 * 往 push plus 里面发送日志
 */
class PushPlusWebhookLogger extends WebhookLogger {

  override protected val proxyHost: String = PushPlusWebhookLogger.proxyHost
  override protected val proxyPort: Int = PushPlusWebhookLogger.proxyPort

  override val loggerConfig: LoggerConfiguration = PushPlusWebhookLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("PushPlusWebhookLogger.loggerConfig is None")
  }

  val loggerTemplate: String = PushPlusWebhookLogger.loggerTemplate match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("template can only be one of html, json, plaintext and cloud_monitor")
  }

  val loggerTopic: Option[String] = PushPlusWebhookLogger.loggerTopic

  /**
   * 要发往的机器人的密钥
   */
  val pushplusRobotsToSend: Set[String] = PushPlusWebhookLogger.robotsToSend.toSet

  override protected def webhookLog(msg: String, level: LogLevel.Value): Boolean = {
    val fullLog = buildLog(msg, level) |> (c =>
      if (loggerTemplate == "html")
        Source.fromResource("gangsutils-logger-html-template.html").mkString + c.toHtmlString + "</body></html>"
      else if (loggerTemplate == "json") {
        c.toJsonString |> stripANSIColor
      } else if (loggerTemplate == "plaintext") {
        c.toStandardLogString |> stripANSIColor
      } else
        throw new IllegalArgumentException("template can only be one of html, json, plaintext and cloud_monitor")
      ) |> escapeQuotationMark
    pushplusRobotsToSend.map(token =>
      sendRequest(s"http://pushplus.hxtrip.com/send",
        body =
          s"""{"token": "$token","title": "$msg", "content": "$fullLog", "template": "$loggerTemplate", "topic": "${
            loggerTopic match {
              case Some(value) => value
              case None => ""
            }
          }"}""")
    ).forall(b => b)
  }

  override protected def checkPrerequisite(): Unit = {
    if (pushplusRobotsToSend.isEmpty || pushplusRobotsToSend.exists(_.isEmpty))
      throw new IllegalArgumentException(I18N.getRB.getString("pushplusWebhookLogger.prerequisite"))
  }
}


/**
 * an object of PushPlusWebhookLogger to set PushPlusWebhookLogger class using
 * <pre>
 * PushPlusWebhookLogger.initializePushplusWebhook(robotsKeys: String)
 * PushPlusWebhookLogger.initializePushplusWebhook(robotsKeys: Array[String])
 * PushPlusWebhookLogger.resetRobotsKeys()
 * PushPlusWebhookLogger.initializeConfiguration(c: LoggerConfiguration)
 * PushPlusWebhookLogger.resetConfiguration()
 * PushPlusWebhookLogger.setLoggerTopic(topic: String)
 * PushPlusWebhookLogger.setLoggerTemplate(template: String)
 * </pre>
 */
object PushPlusWebhookLogger extends WebhookLoggerCompanion {

  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.webhook.PushPlusWebhookLogger"

  /**
   * 要发往的机器人的密钥
   */
  private var robotsToSend: Array[String] = Array.empty[String]

  /**
   * Push Plus logger template parameter
   */
  private var loggerTemplate: Option[String] = None

  /**
   * Push Plus logger topic parameter
   */
  private var loggerTopic: Option[String] = None

  /**
   * set Push Plus logger template parameter,
   *
   * @param template supported values are html, json, plaintext and cloud_monitor
   */
  def setLoggerTemplate(template: String): Unit = {
    if (!Seq("html", "json", "cloud_monitor", "plaintext").contains(template))
      throw new IllegalArgumentException("template can only be one of html, json, plaintext and cloud_monitor")
    loggerTemplate = Some(template)
  }

  /**
   * set Push Plus logger topic parameter
   *
   * @param topic 群组编码
   */
  def setLoggerTopic(topic: String): Unit = loggerTopic = Some(topic)

  def resetRobotsKeys(): Unit = robotsToSend = Array.empty[String]

  /**
   * 初始化 pushplus webhook 的密钥
   *
   * @param robotsKeys 密钥，如果是多个，中间用逗号隔开
   */
  def initializePushplusWebhook(robotsKeys: String): Unit = {
    robotsKeys.split(",").map(_.trim) |! initializePushplusWebhook
  }

  /**
   * 初始化 pushplus webhook 的密钥
   *
   * @param robotsKeys 密钥数组
   */
  def initializePushplusWebhook(robotsKeys: Array[String]): Unit = robotsToSend = {
    if (robotsKeys == null || robotsKeys.isEmpty || robotsKeys.exists(_.isEmpty)) {
      throw new IllegalArgumentException(
        I18N.getRB.getString("pushplusWebhookLogger.initializePushplusWebhook").format(if (robotsKeys == null) null else robotsKeys.mkString("Array(", ", ", ")")))
    }
    robotsKeys
  }

  /**
   * create a new Logger using loggerConfiguration, template, and topic
   *
   * @param c        loggerConfiguration
   * @param template template
   * @param topic    topic
   * @return the expected logger
   */
  def apply(c: LoggerConfiguration, template: String, topic: String): Logger = {
    setLoggerTemplate(template)
    setLoggerTopic(topic)
    apply(c)
  }

  override def apply(): Logger = {
    if (loggerConfig.isEmpty)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    if (loggerTemplate.isEmpty)
      throw new IllegalArgumentException("You haven't invoke setLoggerTemplate(template: String)")
    new PushPlusWebhookLogger()
  }
}