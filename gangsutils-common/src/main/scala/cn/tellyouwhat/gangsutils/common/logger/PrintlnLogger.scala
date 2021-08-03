package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.exceptions.GangException
import cn.tellyouwhat.gangsutils.common.gangfunctions.chainSideEffect

import scala.io.AnsiColor._

trait PrintlnLogger extends BaseLogger {
  protected def printlnLog(msg: String, level: LogLevel.Value): Unit =
    buildLogContent(msg, level) |! { content =>
      level match {
        case LogLevel.ERROR => println(s"$RED$content$RESET")
        case LogLevel.CRITICAL => println(s"$RED$BOLD$content$RESET")
        case LogLevel.WARNING => println(s"$YELLOW$content$RESET")
        case LogLevel.SUCCESS => println(s"$GREEN$content$RESET")
        case LogLevel.INFO => println(s"$BOLD$content$RESET")
        case LogLevel.TRACE => println(content)
        case _ => throw GangException(s"Unknown log level: $level")
      }
    }

  override protected def doTheLogAction(msg: String, level: LogLevel.Value): Unit =
    printlnLog(msg, level)
}

object PrintlnLogger {
  val PRINTLN_LOGGER = "println_logger"
}