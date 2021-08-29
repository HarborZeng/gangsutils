package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.core.funcs.retry
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.WOA_WEBHOOK_LOGGER
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.exceptions.WrongHttpMethodException
import org.scalatest.PrivateMethodTester
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class WebhookLoggerTest extends AnyFlatSpec with Matchers with PrivateMethodTester {

  behavior of "WebhookLoggerTest"

  it should "sendRequest" in {
    val sendRequest = PrivateMethod[Boolean]('sendRequest)
    GangLogger.setLoggerAndConfiguration(Map(
      WOA_WEBHOOK_LOGGER -> LoggerConfiguration()
    ))
    val logger = GangLogger()
    val res = retry(2)(logger.loggers.head.asInstanceOf[WoaWebhookLogger] invokePrivate sendRequest("https://tellyouwhat.cn/google9dee8b8a6358ecc8.html", "GET", "", Seq.empty[(String, String)]))
    res match {
      case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }

    val method = "WRONG HTTP METHOD"
    the[WrongHttpMethodException] thrownBy {
      logger.loggers.head.asInstanceOf[WoaWebhookLogger] invokePrivate sendRequest("", method, "", Seq.empty[(String, String)])
    } should have message s"""${I18N.getRB.getString("sendRequest.wrongHttpMethod").format(method)}"""

    the[IllegalArgumentException] thrownBy {
      logger.loggers.head.asInstanceOf[WoaWebhookLogger] invokePrivate sendRequest("", "POST", "", Seq.empty[(String, String)])
    } should have message "body , queryStrings List(), they can not be empty or non-empty at the same time."
  }

}
