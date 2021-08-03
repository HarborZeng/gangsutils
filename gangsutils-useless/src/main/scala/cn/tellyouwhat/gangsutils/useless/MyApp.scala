package cn.tellyouwhat.gangsutils.useless

import cn.tellyouwhat.gangsutils.common.helper.{Timeit, TimeitLogger}
import cn.tellyouwhat.gangsutils.common.logger.{BaseLogger, GangLogger, LogLevel}
import cn.tellyouwhat.gangsutils.common.logger.SupportedLogDest.PRINTLN_LOGGER

/**
 * 代码实验田
 */
class MyApp extends Timeit {

  private val logger: BaseLogger = MyApp.logger

  override def run(desc: String)(implicit logger: BaseLogger): Unit = {
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

  private implicit var logger: BaseLogger = _

  def apply(): MyApp = new MyApp() with TimeitLogger

  def main(args: Array[String]): Unit = {
    GangLogger.setLogsLevels(Map(PRINTLN_LOGGER -> LogLevel.TRACE))
    GangLogger.disableTrace()

    logger = GangLogger()

    MyApp().run("MyApp instance")
  }
}
