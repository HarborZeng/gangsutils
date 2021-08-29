package cn.tellyouwhat.gangsutils.useless

import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.PRINTLN_LOGGER
import cn.tellyouwhat.gangsutils.logger.{GangLogger, LogLevel, Logger}
import cn.tellyouwhat.gangsutils.logger.helper.{Timeit, TimeitLogger}

/**
 * 代码实验田
 */
class MyApp extends Timeit {

  private val logger: GangLogger = MyApp.logger

  override def run(desc: String): Unit = {
    logger.info("123")
  }

}

object MyApp {

  private implicit var logger: GangLogger = _

  def main(args: Array[String]): Unit = {
    logger = GangLogger(isTraceEnabled = true)
    logger.trace("tracing")

    MyApp().run()
  }

  def apply(): MyApp = new MyApp() with TimeitLogger
}
