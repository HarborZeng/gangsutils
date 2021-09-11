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
    PrintlnLogger.resetConfiguration()
  }

  behavior of "PrintlnLoggerTest"

  it should "printlnLog" in {
    val printlnLog = PrivateMethod[String]('printlnLog)
    val logger = GangLogger.getLogger
    a[WrongLogLevelException] should be thrownBy {
      logger.loggers.head.asInstanceOf[PrintlnLogger] invokePrivate printlnLog("a msg", None, null)
    }
  }

  "PrintlnLogger" should "be newed with an IllegalArgumentException thrown if loggerConfig was not set" in {
    the [IllegalArgumentException] thrownBy new PrintlnLogger() should have message "PrintlnLogger.loggerConfig is None"
  }

  it should "be applied with an IllegalArgumentException thrown if initializeConfiguration(c: LoggerConfiguration) or apply(c: LoggerConfiguration) was not set" in {
    the [IllegalArgumentException] thrownBy PrintlnLogger() should have message "You did not pass parameter loggerConfig nor initializeConfiguration"
  }

}
