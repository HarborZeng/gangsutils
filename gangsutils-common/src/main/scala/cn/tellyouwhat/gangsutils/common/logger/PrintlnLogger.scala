package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.helper.chaining.PipeIt

/**
 * 打印到标准输出的日志，只会打印到 stdout（打印到 stderr 的话会有日志顺序不一致的问题，统一打印到 stdout 更加整齐）
 */
trait PrintlnLogger extends BaseLogger {

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

  override protected def doTheLogAction(msg: String, level: LogLevel.Value): Boolean =
    printlnLog(msg, level)
}

/**
 * 打印到标准输出的日志期伴声对像
 */
object PrintlnLogger {
  /**
   * PRINTLN_LOGGER 字符串
   */
  val PRINTLN_LOGGER = "println_logger"
}