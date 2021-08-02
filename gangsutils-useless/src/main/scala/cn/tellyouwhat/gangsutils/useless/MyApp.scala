package cn.tellyouwhat.gangsutils.useless

import cn.tellyouwhat.gangsutils.common.helper.{Timeit, TimeitLogger}
import cn.tellyouwhat.gangsutils.common.logger.{GangLogger, LogLevel}
import cn.tellyouwhat.gangsutils.common.logger.SupportedLogDest.PRINTLN_LOGGER

class MyApp extends Timeit {

  private val logger: GangLogger = MyApp.logger

  override def run(desc: String)(implicit logger: GangLogger): Unit = {
    Thread.sleep(1000)

    logger.trace("trace")
    logger.info("info")
    logger.success("success")
    logger.warning("warning")
    logger.error("error")
    logger.critical("critical")
  }
}

object MyApp {

  private implicit var logger: GangLogger = _

  def apply(): MyApp = new MyApp() with TimeitLogger

  def main(args: Array[String]): Unit = {
    GangLogger.setLogsLevels(Map(PRINTLN_LOGGER -> LogLevel.TRACE))

    logger = GangLogger()

    MyApp().run("MyApp instance")
  }
}
