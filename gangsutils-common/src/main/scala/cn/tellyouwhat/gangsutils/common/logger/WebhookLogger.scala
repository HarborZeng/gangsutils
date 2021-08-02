package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.exceptions.GangException
import scalaj.http.Http

trait WebhookLogger extends BaseLogger{

  protected def webhookLog(msg: String, level: LogLevel.Value, dt: Boolean, trace: Boolean): Unit

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
