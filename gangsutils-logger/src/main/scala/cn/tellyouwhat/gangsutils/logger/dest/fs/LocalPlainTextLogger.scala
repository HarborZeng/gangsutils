package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.funcs.stripANSIColor
import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger, LoggerCompanion}

import java.io.OutputStream
import java.nio.file.{Path, Paths}

class LocalPlainTextLogger extends LocalFileLogger {

  override private[fs] val logSavePath: Path = LocalPlainTextLogger.logSavePath match {
    case Some(path) => Paths.get(path)
    case None => null
  }

  override val loggerConfig: LoggerConfiguration = LocalPlainTextLogger.loggerConfig

  override def onEOF(os: OutputStream): Unit = {
    //do nothing
  }

  override def onSOF(os: OutputStream): Unit = {
    //do nothing
  }

  override protected def fileLog(msg: String, level: LogLevel.Value): Boolean = {
    buildLog(msg, level).toStandardLogString |> stripANSIColor |> writeString
  }

}

object LocalPlainTextLogger extends LoggerCompanion {

  val LOCAL_PLAIN_TEXT_LOGGER = "cn.tellyouwhat.gangsutils.logger.dest.fs.LocalPlainTextLogger"

  private var loggerConfig: LoggerConfiguration = _

  private var logSavePath: Option[String] = None

  def resetLogSavePath(): Unit = logSavePath = None

  def apply(c: LoggerConfiguration, path: String): Logger = {
    setLogSavePath(path)
    apply(c)
  }

  def setLogSavePath(path: String): Unit = logSavePath = Some(path)

  override def apply(c: LoggerConfiguration): Logger = {
    initializeConfiguration(c)
    apply()
  }

  override def initializeConfiguration(c: LoggerConfiguration): Unit = loggerConfig = c

  override def apply(): Logger = {
    if (loggerConfig == null)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new LocalPlainTextLogger()
  }
}