package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.gangconstants.infoLog
import cn.tellyouwhat.gangsutils.common.helper.I18N.getRB

import java.io.ByteArrayOutputStream
import org.scalatest.PrivateMethodTester
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BaseLoggerTest extends AnyFlatSpec with Matchers with PrivateMethodTester {

  behavior of "BaseLoggerTest"

  "buildLogContent" should "build log using basic content and extra information" in {
    val buildLogContent = PrivateMethod[String]('buildLogContent)
    val logger: BaseLogger = GangLogger(isTraceEnabled = true, isDTEnabled = false)
    val logContent = logger invokePrivate buildLogContent("a msg")
    logContent should fullyMatch regex """ - [\w.]+#[\w.]+""" + getRB.getString("nth_line").format("""\d+""") + ": a msg"
  }

  "log" should "print a log" in {
    val logger: BaseLogger = GangLogger(isTraceEnabled = false, isDTEnabled = false)
    val stream = new ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.log("a info log", LogLevel.INFO)
    }
    stream.toString() should fullyMatch regex infoLog.format(": a info log")
    GangLogger.resetLoggerConfig()
    GangLogger.killLogger()
  }

}
