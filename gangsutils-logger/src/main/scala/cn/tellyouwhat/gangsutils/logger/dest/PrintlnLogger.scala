package cn.tellyouwhat.gangsutils.logger.dest

import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger, LoggerCompanion}

/**
 * 打印到标准输出的日志，只会打印到 stdout（打印到 stderr 的话会有日志顺序不一致的问题，统一打印到 stdout 更加整齐）
 */
class PrintlnLogger extends Logger {

  override val loggerConfig: LoggerConfiguration = PrintlnLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("PrintlnLogger.loggerConfig is None")
  }

  override protected def doTheLogAction(msg: String, level: LogLevel.Value): Boolean = printlnLog(msg, level)

  /**
   * 具体的将内容打印到标准输出
   *
   * @param msg   日志内容
   * @param level 日志级别
   * @return 总是返回 true，除非有异常抛出
   */
  protected def printlnLog(msg: String, level: LogLevel.Value): Boolean = {
    buildLog(msg, level).toString |> println
    true
  }

}

/**
 * an object of PrintlnLogger to set PrintlnLogger class using
 * <pre>
 * PrintlnLogger.resetRobotsKeys()
 * PrintlnLogger.initializeConfiguration(c: LoggerConfiguration)
 * PrintlnLogger.resetConfiguration()
 * </pre>
 */
object PrintlnLogger extends LoggerCompanion {

  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.PrintlnLogger"

  override def apply(): PrintlnLogger = {
    if (loggerConfig.isEmpty)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new PrintlnLogger()
  }
}