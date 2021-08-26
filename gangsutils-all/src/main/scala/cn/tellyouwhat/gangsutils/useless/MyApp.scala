package cn.tellyouwhat.gangsutils.useless

import cn.tellyouwhat.gangsutils.core.helper.chaining.TapIt
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.PRINTLN_LOGGER
import cn.tellyouwhat.gangsutils.logger.{GangLogger, LogLevel, Logger}
import cn.tellyouwhat.gangsutils.logger.helper.{Timeit, TimeitLogger}
import org.apache.spark.sql.SparkSession

import java.sql.SQLDataException

/**
 * 代码实验田
 */
class MyApp extends Timeit {

  private val logger: Logger = MyApp.logger
  private val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
  import spark.implicits._

  override def run(desc: String): Unit = {
    val a = Seq("1", "2", "3", "4", "4", "5").toDF("uid")
    val b = Seq(1, 3, 4, 5, 5, 7).toDF("uid")
    a.join(b, "uid").distinct()
      .tap(_.printSchema())
      .tap(_.show())

  }

  def fun(): Nothing = {
    throw new SQLDataException("haha")
  }
}

object MyApp {

  private implicit var logger: Logger = _

  def main(args: Array[String]): Unit = {
    GangLogger.setLogsLevels(Map(PRINTLN_LOGGER -> LogLevel.TRACE))
    GangLogger.disableTrace()
    logger = GangLogger(isTraceEnabled = true)
    logger.trace("tracing")

    MyApp().run()
  }

  def apply(): MyApp = new MyApp() with TimeitLogger
}
