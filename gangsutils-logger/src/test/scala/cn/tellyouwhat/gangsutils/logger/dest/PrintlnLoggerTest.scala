package cn.tellyouwhat.gangsutils.logger.dest

import cn.tellyouwhat.gangsutils.logger.exceptions.WrongLogLevelException
import cn.tellyouwhat.gangsutils.logger.{GangLogger, Logger}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}

class PrintlnLoggerTest extends AnyFlatSpec with Matchers with PrivateMethodTester with BeforeAndAfter {

  before {
    GangLogger.resetLoggerConfig()
  }

  after {
    GangLogger.resetLoggerConfig()
  }

  behavior of "PrintlnLoggerTest"

  it should "printlnLog" in {
    val printlnLog = PrivateMethod[String]('printlnLog)
    val logger: Logger = GangLogger(isDTEnabled = false)
    a[WrongLogLevelException] should be thrownBy {
      logger invokePrivate printlnLog("a msg", null)
    }
  }

}
