package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.exceptions.WrongLogLevelException
import org.scalatest.PrivateMethodTester
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PrintlnLoggerTest extends AnyFlatSpec with Matchers with PrivateMethodTester {

  behavior of "PrintlnLoggerTest"

  it should "printlnLog" in {
    val printlnLog = PrivateMethod[String]('printlnLog)
    val logger: BaseLogger = GangLogger(isDTEnabled = false)
    a [WrongLogLevelException] should be thrownBy {
      logger invokePrivate printlnLog("a msg", null)
    }
  }

}
