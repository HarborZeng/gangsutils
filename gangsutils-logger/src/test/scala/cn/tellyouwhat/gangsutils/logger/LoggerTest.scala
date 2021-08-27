package cn.tellyouwhat.gangsutils.logger

import cn.tellyouwhat.gangsutils.core.constants.{datetimeRe, infoLog}
import cn.tellyouwhat.gangsutils.logger.cc.OneLog
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}

import java.io.ByteArrayOutputStream

class LoggerTest extends AnyFlatSpec with Matchers with PrivateMethodTester with BeforeAndAfter {

  before {
    GangLogger.disableDateTime()
    GangLogger.disableHostname()
  }

  after {
    GangLogger.killLogger()
    GangLogger.resetLoggerConfig()
  }

  behavior of "LoggerTest"

  "buildLog" should "build log using basic msg, level and other members" in {
    val buildLog = PrivateMethod[OneLog]('buildLog)
    val logger: Logger = GangLogger(isTraceEnabled = true, isDTEnabled = true, isHostnameEnabled = true, logPrefix = Some("a prefix"))
    val logContent = logger invokePrivate buildLog("a msg", LogLevel.INFO)
    logContent.toString + "\n" should fullyMatch regex infoLog.format(
      """ - \S+ - """ + datetimeRe + """ - \S+#\S+ [^:]+: a prefix - a msg"""
    )
  }

  "log" should "print a log" in {
    val logger: Logger = GangLogger(isTraceEnabled = false, isDTEnabled = false)
    val stream = new ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.log("a info log", LogLevel.INFO)
    }
    stream.toString() should fullyMatch regex infoLog.format(": a info log")
  }

}