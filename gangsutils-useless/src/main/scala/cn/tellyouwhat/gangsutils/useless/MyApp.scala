package cn.tellyouwhat.gangsutils.useless

import cn.tellyouwhat.gangsutils.common.gangfunctions.retry
import cn.tellyouwhat.gangsutils.common.helper.{Timeit, TimeitLogger}
import cn.tellyouwhat.gangsutils.common.logger.{BaseLogger, GangLogger, LogLevel}
import cn.tellyouwhat.gangsutils.common.logger.SupportedLogDest.PRINTLN_LOGGER
import org.apache.hadoop.fs.Path

import java.sql.SQLDataException

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
    //    logger.critical("critical")

    logger.info(new Path("path/to", "_SUCCESS").toString)

    retry(2)(fun())
  }

  def fun(): Nothing = {
    throw new SQLDataException("haha")
  }
}

object MyApp {

  private implicit var logger: BaseLogger = _

  def apply(): MyApp = new MyApp() with TimeitLogger

  def main(args: Array[String]): Unit = {
    GangLogger.setLogsLevels(Map(PRINTLN_LOGGER -> LogLevel.TRACE))
    GangLogger.disableTrace()
    logger = GangLogger()

    MyApp().run()
  }
}
