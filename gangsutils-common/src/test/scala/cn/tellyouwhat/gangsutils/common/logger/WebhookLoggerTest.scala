package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.exceptions.WrongHttpMethodException
import cn.tellyouwhat.gangsutils.common.gangfunctions.retry
import org.scalatest.PrivateMethodTester
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.net.SocketTimeoutException
import scala.util.{Failure, Success}

class WebhookLoggerTest extends AnyFlatSpec with Matchers with PrivateMethodTester {

  behavior of "WebhookLoggerTest"

  it should "sendRequest" in {
    val sendRequest = PrivateMethod[Boolean]('sendRequest)
    val logger: WebhookLogger = GangLogger()
    val res = retry(2)(logger invokePrivate sendRequest("https://tellyouwhat.cn/google9dee8b8a6358ecc8.html", "GET", "", Seq.empty[(String, String)]))
    res match {
      case Failure(e) => a [SocketTimeoutException] should be thrownBy (throw e)
      case Success(v) => v shouldBe true
    }

    a [WrongHttpMethodException] should be thrownBy {
      logger invokePrivate sendRequest("", "WRONG HTTP METHOD", "", Seq.empty[(String, String)])
    }
  }

}
