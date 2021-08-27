package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.stripANSIColor
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.LogLevel
import cn.tellyouwhat.gangsutils.logger.cc.Robot
import org.apache.commons.codec.binary.Base64

import java.time.Duration
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

trait FeishuWebhookLogger extends WebhookLogger {

  /**
   * 要发往的机器人的密钥
   */
  val feishuRobotsToSend: Set[Robot] = FeishuWebhookLogger.robotsToSend.toSet

  override protected def webhookLog(msg: String, level: LogLevel.Value): Boolean = {
    val fullLog = buildLog(msg, level).toString |> stripANSIColor
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

object FeishuWebhookLogger {

  /**
   * FEISHU_WEBHOOK_LOGGER 文本
   */
  val FEISHU_WEBHOOK_LOGGER = "feishu_webhook_logger"

  /**
   * 要发往的机器人的密钥
   */
  private var robotsToSend: Array[Robot] = Array.empty[Robot]

  def resetRobots(): Unit = robotsToSend = Array.empty[Robot]

  /**
   * 初始化 feishu webhook 的密钥
   *
   * @param robotsKeysSigns 密钥，如果是多个，中间用逗号隔开
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
    if (robotsKeysSigns == null ||
      robotsKeysSigns.isEmpty ||
      robotsKeysSigns.exists(_.isEmpty) ||
      robotsKeysSigns.exists(_.exists(_.isEmpty)) ||
      robotsKeysSigns.exists(p => p.length > 2 || p.length == 0)
    ) {
      throw new IllegalArgumentException(
        I18N.getRB.getString("feishuWebhookLogger.initializeFeishuWebhook").format(if (robotsKeysSigns == null) null else robotsKeysSigns.mkString("Array(", ", ", ")")))
    }
    robotsKeysSigns.map(keySign => {
      if (keySign.length == 1) {
        val token = keySign.head
        new Robot(Some(token), None)
      } else {
        val token = keySign.head
        val sign = keySign.last
        new Robot(Some(token), Some(sign))
      }
    })
  }
}