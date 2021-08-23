package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.exceptions.WrongHttpMethodException
import cn.tellyouwhat.gangsutils.common.helper.I18N
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
  protected def webhookLog(msg: String, level: LogLevel.Value): Boolean

  /**
   * 发送 http 请求
   *
   * @param targetURL 请求的地址
   * @param method    请求的动词
   * @param body      请求带上的内容
   */
  protected def sendRequest(targetURL: String, method: String = "POST", body: String = ""): Boolean = {
    if (method == "POST") {
      Http(targetURL).header("Content-Type", "application/json").postData(body.replaceAll("""\e\[[\d;]*[^\d;]""","")).asString.isSuccess
    } else if (method == "GET") {
      Http(targetURL).asString.isSuccess
    } else {
      throw WrongHttpMethodException(I18N.getRB.getString("sendRequest.wrongHttpMethod").format(method))
    }
  }

  protected def checkPrerequisite(): Unit

}
