package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger, LoggerCompanion}

import java.io.OutputStream
import java.nio.file.{Path, Paths}
import scala.io.Source

class LocalHtmlLogger extends LocalFileLogger {

  override private[fs] val logSavePath: Path = LocalHtmlLogger.logSavePath match {
    case Some(path) => Paths.get(path)
    case None => null
  }

  override val loggerConfig: LoggerConfiguration = LocalHtmlLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("LocalHtmlLogger.loggerConfig is None")
  }

  override def onEOF(os: OutputStream): Unit = {
    os.write("</body></html>".getBytes("UTF-8"))
    os.flush()
  }

  override def onSOF(os: OutputStream): Unit = {
    os.write(Source.fromResource("gangsutils-logger-html-template.html").mkString.getBytes("UTF-8"))
    os.flush()
  }

  override protected def fileLog(msg: String, level: LogLevel.Value): Boolean = {
    buildLog(msg, level).toHtmlString |> writeString
  }

}

object LocalHtmlLogger extends LoggerCompanion {

  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.fs.LocalHtmlLogger"

  override var loggerConfig: Option[LoggerConfiguration] = None

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

  override def initializeConfiguration(c: LoggerConfiguration): Unit = loggerConfig = Some(c)

  override def apply(): Logger = {
    if (loggerConfig.isEmpty)
      throw new IllegalArgumentException("You did not pass parameter loggerConfig nor initializeConfiguration")
    new LocalHtmlLogger()
  }
}