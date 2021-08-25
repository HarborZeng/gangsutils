package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.helper.I18N.getRB
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LogLevelTest extends AnyFlatSpec with Matchers {

  behavior of "LogLevelTest"

  it should "TRACE" in {
    LogLevel.TRACE.id shouldEqual 0
    LogLevel.TRACE.toString shouldEqual getRB.getString("logLevel.trace")
  }

  it should "INFO" in {
    LogLevel.INFO.id shouldEqual 1
    LogLevel.INFO.toString shouldEqual getRB.getString("logLevel.info")
  }
  it should "SUCCESS" in {
    LogLevel.SUCCESS.id shouldEqual 2
    LogLevel.SUCCESS.toString shouldEqual getRB.getString("logLevel.success")
  }

  it should "WARNING" in {
    LogLevel.WARNING.id shouldEqual 3
    LogLevel.WARNING.toString shouldEqual getRB.getString("logLevel.warning")
  }

  it should "ERROR" in {
    LogLevel.ERROR.id shouldEqual 4
    LogLevel.ERROR.toString shouldEqual getRB.getString("logLevel.error")
  }

  it should "CRITICAL" in {
    LogLevel.CRITICAL.id shouldEqual 5
    LogLevel.CRITICAL.toString shouldEqual getRB.getString("logLevel.critical")
  }

}
