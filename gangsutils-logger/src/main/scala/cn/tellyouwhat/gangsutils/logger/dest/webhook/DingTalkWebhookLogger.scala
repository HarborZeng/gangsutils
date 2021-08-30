package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.stripANSIColor
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.cc.{LoggerConfiguration, Robot}
import cn.tellyouwhat.gangsutils.logger.{LogLevel, LoggerCompanion}
import org.apache.commons.codec.binary.Base64

import java.net.URLEncoder
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class DingTalkWebhookLogger extends WebhookLogger {

  override val loggerConfig: LoggerConfiguration = DingTalkWebhookLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("DingTalkWebhookLogger.loggerConfig is None")
  }

  /**
   * 要发往的机器人的密钥
   */
  val dingTalkRobotsToSend: Set[Robot] = DingTalkWebhookLogger.robotsToSend.toSet

  override protected def webhookLog(msg: String, level: LogLevel.Value): Boolean = {
    val fullLog = buildLog(msg, level).toString |> stripANSIColor
    dingTalkRobotsToSend.map(robot => {
      val t = System.currentTimeMillis
      val targetURL = robot.sign match {
        case Some(secret) =>
          val stringToSign = t + "\n" + secret
          val mac = Mac.getInstance("HmacSHA256")
          mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"))
          val signData = mac.doFinal(stringToSign.getBytes("UTF-8"))
          val sign = URLEncoder.encode(Base64.encodeBase64String(signData), "UTF-8")
          s"https://oapi.dingtalk.com/robot/send?access_token=${robot.token.get}&timestamp=$t&sign=$sign"
        case None =>
          s"https://oapi.dingtalk.com/robot/send?access_token=${robot.token.get}"
      }
      sendRequest(targetURL, body = s"""{"msgtype": "text","text": {"content": "$fullLog"}}""")
    }).forall(b => b)
  }

  override protected def checkPrerequisite(): Unit =
    if (dingTalkRobotsToSend.isEmpty)
      throw new IllegalArgumentException(I18N.getRB.getString("dingTalkWebhookLogger.prerequisite"))
}

object DingTalkWebhookLogger extends LoggerCompanion {

  /**
   * DINGTALK_WEBHOOK_LOGGER 文本
   */
  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.webhook.DingTalkWebhookLogger"

  override private[logger] var loggerConfig: Option[LoggerConfiguration] = None
  /**
   * 要发往的机器人的密钥
   */
  private var robotsToSend: Array[Robot] = Array.empty[Robot]

  def resetRobots(): Unit = robotsToSend = Array.empty[Robot]

  /**
   * 初始化 dingtalk webhook 的密钥
   *
   * @param robotsKeysSigns 密钥，如果是多个，中间用逗号隔开
   */
  def initializeDingTalkWebhook(robotsKeysSigns: String): Unit = {
    robotsKeysSigns.split(",").map(_.trim.split(";").map(_.trim)) |! initializeDingTalkWebhook
  }

  /**
   * 初始化 dingtalk webhook 的密钥
   *
   * @param robotsKeysSigns 密钥数组
   */
  def initializeDingTalkWebhook(robotsKeysSigns: Array[Array[String]]): Unit = robotsToSend = {
    if (robotsKeysSigns == null ||
      robotsKeysSigns.isEmpty ||
      robotsKeysSigns.exists(_.isEmpty) ||
      robotsKeysSigns.exists(_.exists(_.isEmpty)) ||
      robotsKeysSigns.exists(p => p.length > 2 || p.length == 0)
    ) {
      throw new IllegalArgumentException(
        I18N.getRB.getString("dingTalkWebhookLogger.initializeDingTalkWebhook").format(if (robotsKeysSigns == null) null else robotsKeysSigns.map(_.mkString("Array(", ", ", ")")).mkString("Array(", ", ", ")")))
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

  override def apply(c: LoggerConfiguration): DingTalkWebhookLogger = {
    initializeConfiguration(c)
    apply()
  }

  override def initializeConfiguration(c: LoggerConfiguration): Unit = loggerConfig = Some(c)

  override def resetConfiguration(): Unit = loggerConfig = None

  override def apply(): DingTalkWebhookLogger = {
    if (loggerConfig.isEmpty)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new DingTalkWebhookLogger()
  }
}