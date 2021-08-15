package cn.tellyouwhat.gangsutils.common.helper

import cn.tellyouwhat.gangsutils.common.logger.{BaseLogger, GangLogger}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TimeitLoggerTest extends AnyFlatSpec with Matchers {

  "timeit logger run without logger instance" should "run a method and print the start, end and time duration to invoke that method" in {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      class TL extends Timeit {
        override def run(desc: String)(implicit logger: BaseLogger): Unit = {

        }
      }
      object TL {
        def tl(): Unit = {
          val o = new TL with TimeitLogger
          o.run()
        }
      }
      TL.tl()
    }
    stream.toString should fullyMatch regex
      """【跟踪】: 开始任务\s+【成功】: 完成任务，耗时\d*\.*\d*s\s+""".r
  }

  "timeit logger run with logger instance" should "run a method and log the start, end and time duration to invoke that method" in {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      class TL extends Timeit {
        override def run(desc: String)(implicit logger: BaseLogger): Unit = {

        }
      }
      object TL {
        def tl(): Unit = {
          val o = new TL with TimeitLogger
          implicit val logger: BaseLogger = GangLogger(isDTEnabled = true, isTraceEnabled = false)
          o.run()
        }
      }
      TL.tl()
    }
    stream.toString should fullyMatch regex
          """【跟踪】 - \d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d+: 开始任务\s+\u001b\[32m【成功】 - \d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d+: 完成任务，耗时\d*\.*\d+s\u001b\[0m\s+""".r
  }


}
