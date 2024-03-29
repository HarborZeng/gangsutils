package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.{escapeJsonString, stripANSIColor}
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.cc.{LoggerConfiguration, Robot}
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger}
import org.apache.commons.codec.binary.Base64

import java.time.Duration
import java.util.Objects
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * A logger that write logs to Feishu (飞书)
 */
class FeishuWebhookLogger extends WebhookLogger {

  override protected val proxyHost: Option[String] = FeishuWebhookLogger.proxyHost
  override protected val proxyPort: Option[Int] = FeishuWebhookLogger.proxyPort

  override val loggerConfig: LoggerConfiguration = FeishuWebhookLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("FeishuWebhookLogger.loggerConfig is None")
  }

  /**
   * 要发往的机器人
   */
  val feishuRobotsToSend: Set[Robot] = FeishuWebhookLogger.robotsToSend.toSet

  override protected def webhookLog(msg: String, optionThrowable: Option[Throwable], level: LogLevel.Value): Boolean = {
    val fullLog = buildLog(msg, optionThrowable, level).toString |> stripANSIColor |> escapeJsonString
    feishuRobotsToSend.map(robot => {
      // feishu use second as timestamp
      val t = Duration.ofMillis(System.currentTimeMillis()).getSeconds
      val body = robot.sign match {
        case Some(secret) =>
          val stringToSign = t + "\n" + secret
          val mac = Mac.getInstance("HmacSHA256")
          mac.init(new SecretKeySpec(stringToSign.getBytes(), "HmacSHA256"))
          val signData = mac.doFinal()
          val sign = Base64.encodeBase64String(signData)
          s"""{"timestamp": "$t", "sign": "$sign", "msg_type":"text","content":{"text":"$fullLog"}}"""
        case None =>
          s"""{"msg_type":"text","content":{"text":"$fullLog"}}"""
      }
      sendRequest(s"https://open.feishu.cn/open-apis/bot/v2/hook/${robot.token.get}", body = body)
    }).forall(b => b)
  }

  override protected def checkPrerequisite(): Unit =
    if (feishuRobotsToSend.isEmpty)
      throw new IllegalArgumentException(I18N.getRB.getString("feishuWebhookLogger.prerequisite"))
}

/**
 * an object of FeishuWebhookLogger to set FeishuWebhookLogger class using
 * <pre>
 * FeishuWebhookLogger.initializeFeishuWebhook(robotsKeysSigns: String)
 * FeishuWebhookLogger.initializeFeishuWebhook(robotsKeysSigns: Array[Array[String]])
 * FeishuWebhookLogger.resetRobots()
 * FeishuWebhookLogger.initializeConfiguration(c: LoggerConfiguration)
 * FeishuWebhookLogger.resetConfiguration()
 * </pre>
 */
object FeishuWebhookLogger extends WebhookLoggerCompanion {

  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.webhook.FeishuWebhookLogger"

  /**
   * 要发往的机器人的密钥
   */
  private var robotsToSend: Array[Robot] = Array.empty[Robot]

  def resetRobots(): Unit = robotsToSend = Array.empty[Robot]

  /**
   * 初始化 feishu webhook 的密钥和签名
   *
   * @param robotsKeysSigns 密钥;签名，如果是多个，中间用逗号隔开，密钥和签名之间用分号隔开
   */
  def initializeFeishuWebhook(robotsKeysSigns: String): Unit = {
    robotsKeysSigns.split(",").map(_.trim.split(";").map(_.trim)) |! initializeFeishuWebhook
  }

  /**
   * 初始化 feishu webhook 的密钥
   *
   * @param robotsKeysSigns 密钥数组
   */
  def initializeFeishuWebhook(robotsKeysSigns: Array[Array[String]]): Unit = robotsToSend = {
    Objects.requireNonNull(robotsKeysSigns)
    if (robotsKeysSigns.isEmpty ||
      robotsKeysSigns.exists(_.isEmpty) ||
      robotsKeysSigns.exists(_.exists(_.isEmpty)) ||
      robotsKeysSigns.exists(p => p.length > 2 || p.length == 0)
    ) {
      throw new IllegalArgumentException(
        I18N.getRB.getString("feishuWebhookLogger.initializeFeishuWebhook").format(robotsKeysSigns.map(_.mkString("Array(", ", ", ")")).mkString("Array(", ", ", ")")))
    }
    robotsKeysSigns.map(keySign => {
      if (keySign.length == 1) {
        val token = keySign.head
        Robot(Some(token), None)
      } else {
        val token = keySign.head
        val sign = keySign.last
        Robot(Some(token), Some(sign))
      }
    })
  }

  override def apply(): Logger = {
    if (loggerConfig.isEmpty)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new FeishuWebhookLogger()
  }
}