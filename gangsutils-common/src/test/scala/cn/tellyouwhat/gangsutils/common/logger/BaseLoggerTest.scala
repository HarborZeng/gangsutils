package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.gangconstants.{datetimeRe, infoLog}

import java.io.ByteArrayOutputStream
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BaseLoggerTest extends AnyFlatSpec with Matchers with PrivateMethodTester with BeforeAndAfter {

  before {
    GangLogger.disableDateTime()
    GangLogger.disableHostname()
  }

  after {
    GangLogger.killLogger()
    GangLogger.resetLoggerConfig()
  }

  behavior of "BaseLoggerTest"

  "buildLog" should "build log using basic msg, level and other members" in {
    val buildLog = PrivateMethod[OneLog]('buildLog)
    val logger: BaseLogger = GangLogger(isTraceEnabled = true, isDTEnabled = true, isHostnameEnabled = true, logPrefix = Some("a prefix"))
    val logContent = logger invokePrivate buildLog("a msg", LogLevel.INFO)
    logContent.toString + "\n" should fullyMatch regex infoLog.format(
      """ - \S+ - """ + datetimeRe + """ - \S+#\S+ [^:]+: a prefix - a msg"""
    )
  }

  "log" should "print a log" in {
    val logger: BaseLogger = GangLogger(isTraceEnabled = false, isDTEnabled = false)
    val stream = new ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.log("a info log", LogLevel.INFO)
    }
    stream.toString() should fullyMatch regex infoLog.format(": a info log")
  }

}
