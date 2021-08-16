package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.exceptions.WrongLogLevelException
import cn.tellyouwhat.gangsutils.common.helper.chaining.PipeIt

import scala.io.AnsiColor._

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
  protected def printlnLog(msg: String, level: LogLevel.Value): Boolean =
    buildLogContent(msg, level) |> { content =>
      level match {
        case LogLevel.ERROR => println(s"$RED$content$RESET")
        case LogLevel.CRITICAL => println(s"$RED$BOLD$content$RESET")
        case LogLevel.WARNING => println(s"$YELLOW$content$RESET")
        case LogLevel.SUCCESS => println(s"$GREEN$content$RESET")
        case LogLevel.INFO => println(s"$BOLD$content$RESET")
        case LogLevel.TRACE => println(content)
        case _ => throw WrongLogLevelException(s"Unknown log level: $level")
      }
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