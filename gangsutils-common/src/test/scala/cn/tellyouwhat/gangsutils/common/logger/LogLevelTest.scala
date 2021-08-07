package cn.tellyouwhat.gangsutils.common.logger

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LogLevelTest extends AnyFlatSpec with Matchers {

  behavior of "LogLevelTest"

  it should "TRACE" in {
    LogLevel.TRACE.id shouldEqual 0
    LogLevel.TRACE.toString shouldEqual "跟踪"
  }

  it should "INFO" in {
    LogLevel.INFO.id shouldEqual 1
    LogLevel.INFO.toString shouldEqual "信息"
  }
  it should "SUCCESS" in {
    LogLevel.SUCCESS.id shouldEqual 2
    LogLevel.SUCCESS.toString shouldEqual "成功"
  }

  it should "WARNING" in {
    LogLevel.WARNING.id shouldEqual 3
    LogLevel.WARNING.toString shouldEqual "警告"
  }

  it should "ERROR" in {
    LogLevel.ERROR.id shouldEqual 4
    LogLevel.ERROR.toString shouldEqual "错误"
  }

  it should "CRITICAL" in {
    LogLevel.CRITICAL.id shouldEqual 5
    LogLevel.CRITICAL.toString shouldEqual "致命"
  }

}
