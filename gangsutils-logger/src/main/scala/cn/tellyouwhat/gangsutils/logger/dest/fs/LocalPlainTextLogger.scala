package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.funcs.stripANSIColor
import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.logger.LogLevel

trait LocalPlainTextLogger extends LocalFileLogger{
  override protected def fileLog(msg: String, level: LogLevel.Value): Boolean = {
    buildLog(msg, level).toStandardLogString |> stripANSIColor |> writeString
  }
}

object LocalPlainTextLogger {
  val LOCAL_PLAIN_TEXT_LOGGER = "local_plain_text_logger"

  def setLogSavePath(path: String): Unit = LocalFileLogger.setLogSavePath(path)

}