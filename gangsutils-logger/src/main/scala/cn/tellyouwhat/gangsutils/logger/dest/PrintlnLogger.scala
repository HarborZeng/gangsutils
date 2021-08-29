package cn.tellyouwhat.gangsutils.logger.dest

import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger, LoggerCompanion}

/**
 * 打印到标准输出的日志，只会打印到 stdout（打印到 stderr 的话会有日志顺序不一致的问题，统一打印到 stdout 更加整齐）
 */
class PrintlnLogger(
                               override val loggerConfig: LoggerConfiguration = PrintlnLogger.loggerConfig
                             ) extends Logger {

  override protected def doTheLogAction(msg: String, level: LogLevel.Value): Boolean = printlnLog(msg, level)

  /**
   * 具体的将内容打印到标准输出
   *
   * @param msg   日志内容
   * @param level 日志级别
   */
  protected def printlnLog(msg: String, level: LogLevel.Value): Boolean = {
    buildLog(msg, level).toString |> println
    true
  }

}

/**
 * 打印到标准输出的日志期伴声对像
 */
object PrintlnLogger extends LoggerCompanion {
  /**
   * PRINTLN_LOGGER 字符串
   */
  val PRINTLN_LOGGER = "cn.tellyouwhat.gangsutils.logger.dest.PrintlnLogger"


  private var loggerConfig: LoggerConfiguration = _

  override def initializeConfiguration(c: LoggerConfiguration): Unit = loggerConfig = c

  override def apply(c: LoggerConfiguration): PrintlnLogger = {
    loggerConfig = c
    apply()
  }

  override def apply(): PrintlnLogger = {
    if (loggerConfig == null)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new PrintlnLogger()
  }
}