package cn.tellyouwhat.gangsutils.logger.dest

import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.PRINTLN_LOGGER
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.exceptions.WrongLogLevelException
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}

class PrintlnLoggerTest extends AnyFlatSpec with Matchers with PrivateMethodTester with BeforeAndAfter {

  before {
    GangLogger.setLoggerAndConfiguration(Map(
      PRINTLN_LOGGER -> LoggerConfiguration()
    ))
  }
  after {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
  }

  behavior of "PrintlnLoggerTest"

  it should "printlnLog" in {
    val printlnLog = PrivateMethod[String]('printlnLog)
    val logger = GangLogger.getLogger
    a[WrongLogLevelException] should be thrownBy {
      logger.loggers.head.asInstanceOf[PrintlnLogger] invokePrivate printlnLog("a msg", null)
    }
  }

}
