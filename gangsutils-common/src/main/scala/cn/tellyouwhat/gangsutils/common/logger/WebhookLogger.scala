package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.exceptions.WrongHttpMethodException
import cn.tellyouwhat.gangsutils.common.helper.I18N
import cn.tellyouwhat.gangsutils.common.logger.SupportedLogDest.PRINTLN_LOGGER
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
  protected def sendRequest(targetURL: String, method: String = "POST", body: String = "", queryStrings: Seq[(String, String)] = Seq.empty[(String, String)]): Boolean = {
    val response = if (method == "POST") {
      if (body.isEmpty && queryStrings.nonEmpty) {
        Http(targetURL)
          .postForm(queryStrings)
          .asString
      } else if (body.nonEmpty && queryStrings.isEmpty) {
        Http(targetURL)
          .header("Content-Type", "application/json")
          .postData(body)
          .asString
      } else {
        throw new IllegalArgumentException(s"body $body, queryStrings $queryStrings, they can not be empty or non-empty at the same time.")
      }
    } else if (method == "GET") {
      Http(targetURL)
        .asString
    } else {
      throw WrongHttpMethodException(I18N.getRB.getString("sendRequest.wrongHttpMethod").format(method))
    }
    // some webhook get error response but with 200 Http Status code, so match them here and return false
    if (response.isSuccess && Seq(
    """"errcode":300001""", // DingTalk
    """"code":19001""", // feishu
    """"errcode":93000""", // qywx(企业微信)
    ).exists(response.body.contains)) {
      GangLogger.getLogger.critical(new IllegalArgumentException(s"sendRequest response body is wrong: ${response.body}"))(enabled = Seq(PRINTLN_LOGGER))
      return false
    }
    response.isSuccess
  }

  protected def checkPrerequisite(): Unit

}
