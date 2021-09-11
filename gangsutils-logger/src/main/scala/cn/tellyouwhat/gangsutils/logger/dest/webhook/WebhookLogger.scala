package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.exceptions.GangException
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.logger.exceptions.WrongHttpMethodException
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger}
import scalaj.http.Http

/**
 * Webhook 日志器特质
 */
trait WebhookLogger extends Logger {

  /**
   * proxy host
   */
  protected val proxyHost: Option[String] = None

  /**
   * proxy port
   */
  protected val proxyPort: Option[Int] = None

  /**
   * 执行 webhook 日志
   *
   * @param msg   日志内容
   * @param level 日志级别
   */
  protected def webhookLog(msg: String, optionThrowable: Option[Throwable], level: LogLevel.Value): Boolean

  /**
   * 发送 http 请求
   *
   * @param targetURL 请求的地址
   * @param method    请求的动词
   * @param body      请求带上的内容
   */
  private[logger] def sendRequest(targetURL: String, method: String = "POST", body: String = "", form: Seq[(String, String)] = Seq.empty[(String, String)]): Boolean = {
    val httpRequest = Http(targetURL)
      .pipe(r => if (proxyHost.nonEmpty && proxyPort.nonEmpty) r.proxy(proxyHost.get, proxyPort.get) else r)
    val response = if (method == "POST") {
      if (body.isEmpty && form.nonEmpty) {
        httpRequest
          .postForm(form)
          .asString
      } else if (body.nonEmpty && form.isEmpty) {
        httpRequest
          .header("Content-Type", "application/json")
          .postData(body)
          .asString
      } else {
        throw new IllegalArgumentException(s"body $body, queryStrings $form, they can not be empty or non-empty at the same time.")
      }
    } else if (method == "GET") {
      httpRequest.asString
    } else {
      throw WrongHttpMethodException(I18N.getRB.getString("sendRequest.wrongHttpMethod").format(method))
    }
    // some webhooks get error response but with 200 Http Status code, so match them here and return false
    if (response.isSuccess && Seq(
      """"errcode":300001""", // DingTalk token is not exist
      """"code":19001""", // feishu param invalid: incoming webhook access token invalid
      """"errcode":93000""", // qywx(企业微信) invalid webhook url, hint ......
      """"code":600""", // push plus 用户信息状态不合法
    ).exists(response.body.contains)) {
      println(new IllegalArgumentException(s"sendRequest response body is wrong: ${response.body}"))
      return false
    }
    if (response.isError) {
      println(GangException(s"send logger response is error: ${response.code}, response body: ${response.body}, request body: $body, request form: $form, targetURL: $targetURL"))
    }
    response.isSuccess
  }

  override protected def doTheLogAction(msg: String, optionThrowable: Option[Throwable], level: LogLevel.Value): Boolean = webhookLog(msg, optionThrowable, level)

}
