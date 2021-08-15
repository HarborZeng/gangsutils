package cn.tellyouwhat.gangsutils.common.logger

import java.io.ByteArrayOutputStream
import org.scalatest.PrivateMethodTester
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BaseLoggerTest extends AnyFlatSpec with Matchers with PrivateMethodTester {

  behavior of "BaseLoggerTest"

  "buildLogContent" should "build log using basic content and extra information" in {
    val buildLogContent = PrivateMethod[String]('buildLogContent)
    val logger: BaseLogger = GangLogger(isTraceEnabled = true, isDTEnabled = false)
    val logContent = logger invokePrivate buildLogContent("a msg", LogLevel.TRACE)
    logContent should fullyMatch regex """【跟踪】 - [\w.]+#[\w.]+第\d+行: a msg""".r
  }

  "log" should "print a log" in {
    val logger: BaseLogger = GangLogger(isTraceEnabled = false, isDTEnabled = false)
    val stream = new ByteArrayOutputStream()
    Console.withOut(stream) {
      logger.log("a info log", LogLevel.INFO)
    }
    stream.toString() should fullyMatch regex """\u001b\[1m【信息】: a info log\u001b\[0m\s+""".r
    GangLogger.resetLoggerConfig()
    GangLogger.killLogger()
  }

}
