package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.funcs.stripANSIColor
import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger, LoggerCompanion}

import java.io.OutputStream
import java.nio.file.{Path, Paths}

/**
 * A logger that write plain text to files
 */
class LocalPlainTextLogger extends LocalFileLogger {

  override private[fs] val logSavePath: Path = LocalPlainTextLogger.logSavePath match {
    case Some(path) => Paths.get(path)
    case None => throw new IllegalArgumentException("LocalPlainTextLogger.logSavePath is None")
  }

  override val loggerConfig: LoggerConfiguration = LocalPlainTextLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("LocalPlainTextLogger.loggerConfig is None")
  }

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

/**
 * an object of LocalPlainTextLogger to set LocalPlainTextLogger class using
 * <pre>
 * LocalPlainTextLogger.setLogSavePath(path: String)
 * LocalPlainTextLogger.resetLogSavePath()
 * LocalPlainTextLogger.initializeConfiguration(c: LoggerConfiguration)
 * LocalPlainTextLogger.resetConfiguration()
 * </pre>
 */
object LocalPlainTextLogger extends LoggerCompanion {

  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.fs.LocalPlainTextLogger"

  /**
   * the file path to save plain text log
   */
  private var logSavePath: Option[String] = None

  /**
   * set the file path to save plain text log
   */
  def setLogSavePath(path: String): Unit = logSavePath = Some(path)

  /**
   * reset the file path to None
   */
  def resetLogSavePath(): Unit = logSavePath = None

  /**
   * create a new LocalPlainTextLogger with LoggerConfiguration and path
   *
   * @param c    LoggerConfiguration
   * @param path file path to save html log
   * @return
   */
  def apply(c: LoggerConfiguration, path: String): Logger = {
    setLogSavePath(path)
    apply(c)
  }

  override def apply(): Logger = {
    if (loggerConfig.isEmpty)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new LocalPlainTextLogger()
  }
}