package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.exceptions.GangException
import scalaj.http.Http

/**
 * Webhook 日志器特质
 */
trait WebhookLogger extends BaseLogger {

  /**
   * 执行 webhook 日志
   *
   * @param msg   日志内容
   * @param level 日志级别
   */
  protected def webhookLog(msg: String, level: LogLevel.Value): Unit

  /**
   * 发送 http 请求
   *
   * @param targetURL 请求的地址
   * @param method    请求的动词
   * @param body      请求带上的内容
   */
  protected def sendRequest(targetURL: String, method: String = "POST", body: String = ""): Unit = {
    if (method == "POST") {
      Http(targetURL).postData(body).asString
    } else if (method == "GET") {
      Http(targetURL).asString
    } else {
      throw GangException("错误的 HTTP METHOD")
    }
  }
}
