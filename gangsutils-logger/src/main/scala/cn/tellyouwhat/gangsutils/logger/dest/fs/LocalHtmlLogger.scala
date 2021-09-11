package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger, LoggerCompanion}

import java.io.OutputStream
import java.nio.file.{Path, Paths}
import scala.io.Source

/**
 * A logger that write colorful html to files
 *
 * <pre>
 * &lt;div class="log"&gt;&lt;div class="head"&gt;【跟踪】&lt;/div&gt;&lt;pre&gt; - GANG-PC - 2021-08-31T23:17:14.913215700 - cn.tellyouwhat.gangsutils.logger.Logger#buildLog 第96行: prefix - abc&lt;/pre&gt;&lt;/div&gt;
 * &lt;div class="log"&gt;&lt;div class="head info"&gt;【信息】&lt;/div&gt;&lt;pre&gt; - GANG-PC - 2021-08-31T23:17:14.920216600 - cn.tellyouwhat.gangsutils.logger.Logger#buildLog 第96行: prefix - abc&lt;/pre&gt;&lt;/div&gt;
 * &lt;div class="log"&gt;&lt;div class="head success"&gt;【成功】&lt;/div&gt;&lt;pre&gt; - GANG-PC - 2021-08-31T23:17:14.921216200 - cn.tellyouwhat.gangsutils.logger.Logger#buildLog 第96行: prefix - abc&lt;/pre&gt;&lt;/div&gt;
 * &lt;div class="log"&gt;&lt;div class="head error"&gt;【错误】&lt;/div&gt;&lt;pre&gt; - GANG-PC - 2021-08-31T23:17:14.923216 - cn.tellyouwhat.gangsutils.logger.Logger#buildLog 第96行: prefix - abc&lt;/pre&gt;&lt;/div&gt;
 * &lt;div class="log"&gt;&lt;div class="head warning"&gt;【警告】&lt;/div&gt;&lt;pre&gt; - GANG-PC - 2021-08-31T23:17:14.924215600 - cn.tellyouwhat.gangsutils.logger.Logger#buildLog 第96行: prefix - abc&lt;/pre&gt;&lt;/div&gt;
 * &lt;div class="log"&gt;&lt;div class="head critical"&gt;【致命】&lt;/div&gt;&lt;pre&gt; - GANG-PC - 2021-08-31T23:17:14.926215700 - cn.tellyouwhat.gangsutils.logger.Logger#buildLog 第96行: prefix - abc&lt;/pre&gt;&lt;/div&gt;
 * </pre>
 */
class LocalHtmlLogger extends LocalFileLogger {

  override private[fs] val logSavePath: Path = LocalHtmlLogger.logSavePath match {
    case Some(path) => Paths.get(path)
    case None => throw new IllegalArgumentException("LocalHtmlLogger.logSavePath is None")
  }

  override val loggerConfig: LoggerConfiguration = LocalHtmlLogger.loggerConfig match {
    case Some(value) => value
    case None => throw new IllegalArgumentException("LocalHtmlLogger.loggerConfig is None")
  }

  override def onEOF(os: OutputStream): Unit = {
    // write enclosing tag of html, which is actually not necessary, because the compatibility of web browser
    os.write("</body></html>".getBytes("UTF-8"))
    os.flush()
  }

  override def onSOF(os: OutputStream): Unit = {
    // read from template file and write to the log file
    os.write(Source.fromResource("gangsutils-logger-html-template.html").mkString.getBytes("UTF-8"))
    os.flush()
  }

  override protected def fileLog(msg: String, optionThrowable: Option[Throwable], level: LogLevel.Value): Boolean = {
    buildLog(msg, optionThrowable, level).toHtmlString |> writeString
  }

}

/**
 * an object of LocalHtmlLogger to set LocalHtmlLogger class using
 * <pre>
 * LocalHtmlLogger.setLogSavePath(path: String)
 * LocalHtmlLogger.resetLogSavePath()
 * LocalHtmlLogger.initializeConfiguration(c: LoggerConfiguration)
 * LocalHtmlLogger.resetConfiguration()
 * </pre>
 */
object LocalHtmlLogger extends LoggerCompanion {

  override val loggerName: String = "cn.tellyouwhat.gangsutils.logger.dest.fs.LocalHtmlLogger"

  /**
   * the file path to save html log
   */
  private var logSavePath: Option[String] = None

  /**
   * set the file path to save html log
   */
  def setLogSavePath(path: String): Unit = logSavePath = Some(path)

  /**
   * reset the file path to None
   */
  def resetLogSavePath(): Unit = logSavePath = None

  /**
   * create a new LocalHtmlLogger with LoggerConfiguration and path
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
    new LocalHtmlLogger()
  }
}