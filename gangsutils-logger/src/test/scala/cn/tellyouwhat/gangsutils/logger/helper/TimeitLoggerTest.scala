package cn.tellyouwhat.gangsutils.logger.helper

import cn.tellyouwhat.gangsutils.core.constants.{datetimeRe, successLog, traceLog}
import cn.tellyouwhat.gangsutils.core.helper.I18N.getRB
import cn.tellyouwhat.gangsutils.logger.GangLogger
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TimeitLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {
  before {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
  }

  after {
    GangLogger.killLogger()
    GangLogger.clearLogger2Configuration()
  }

  "timeit logger run without logger instance" should "run a method and print the start, end and time duration to invoke that method" in {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      class TL extends Timeit {
        override def run(desc: String): Unit = {
          // doing nothing
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
      """.+\s+""" +
        traceLog.format(s""" - .+ - $datetimeRe: """ + getRB.getString("timeit.start").format(getRB.getString("task"))) +
        successLog.format(s""" - .+ - $datetimeRe: """ + getRB.getString("timeit.finished").format(getRB.getString("task"), """\d*\.*\d*s"""))
  }

  "timeit logger run with logger instance" should "run a method and log the start, end and time duration to invoke that method" in {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      class TL extends Timeit {
        override def run(desc: String): Unit = {
          // doing nothing
        }
      }
      object TL {
        def tl(): Unit = {
          val o = new TL with TimeitLogger
          implicit val logger: GangLogger = GangLogger.getLogger
          o.run()
        }
      }
      TL.tl()
    }
    stream.toString should fullyMatch regex
    """.+\s+""" +
      traceLog.format(s""" - .+ - $datetimeRe: """ + getRB.getString("timeit.start").format(getRB.getString("task"))) +
      successLog.format(s""" - .+ - $datetimeRe: """ + getRB.getString("timeit.finished").format(getRB.getString("task"), """\d*\.*\d*s"""))
  }


}
