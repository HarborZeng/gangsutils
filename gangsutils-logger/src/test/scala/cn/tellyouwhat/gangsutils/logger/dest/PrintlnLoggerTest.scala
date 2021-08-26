package cn.tellyouwhat.gangsutils.logger.dest

import cn.tellyouwhat.gangsutils.logger.exceptions.WrongLogLevelException
import cn.tellyouwhat.gangsutils.logger.{GangLogger, Logger}
import org.scalatest.PrivateMethodTester
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PrintlnLoggerTest extends AnyFlatSpec with Matchers with PrivateMethodTester {

  behavior of "PrintlnLoggerTest"

  it should "printlnLog" in {
    val printlnLog = PrivateMethod[String]('printlnLog)
    val logger: Logger = GangLogger(isDTEnabled = false)
    a[WrongLogLevelException] should be thrownBy {
      logger invokePrivate printlnLog("a msg", null)
    }
  }

}
