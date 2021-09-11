package cn.tellyouwhat.gangsutils.logger.cc

import cn.tellyouwhat.gangsutils.core.constants._
import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.logger.LogLevel
import cn.tellyouwhat.gangsutils.logger.exceptions.WrongLogLevelException
import io.circe.Encoder
import io.circe.syntax._

import java.io.{PrintWriter, StringWriter}
import java.time.LocalDateTime
import scala.io.AnsiColor._

/**
 * OneLog case class is for storing a real log, including level, hostname, datetime, className, methodName, lineNumber, prefix and msg
 *
 * @param level      option log level of [[LogLevel]]
 * @param hostname   option hostname of String
 * @param datetime   option datetime of [[LocalDateTime]]
 * @param className  option className of String
 * @param methodName option methodName of String
 * @param fileName   option fileName of String
 * @param lineNumber option lineNumber of Int
 * @param prefix     option prefix of String
 * @param msg        option msg of String, the real log content
 */
case class OneLog(
                   level: Option[LogLevel.Value],
                   hostname: Option[String],
                   datetime: Option[LocalDateTime],
                   className: Option[String],
                   methodName: Option[String],
                   fileName: Option[String],
                   lineNumber: Option[Int],
                   prefix: Option[String],
                   msg: Option[String],
                   throwable: Option[Throwable],
                 ) {

  /**
   * toJsonString can convert this case class to json string without space
   *
   * @return a json string without space
   */
  def toJsonString: String = {
    this.asJson(Encoder.forProduct10(
      "level",
      "hostname",
      "datetime",
      "className",
      "methodName",
      "fileName",
      "lineNumber",
      "prefix",
      "msg",
      "throwable"
    )(u => (
      u.level.orNull.toString,
      u.hostname,
      u.datetime,
      u.className,
      u.methodName,
      u.fileName,
      u.lineNumber,
      u.prefix,
      u.msg,
      renderThrowable()
    ))).noSpaces
  }

  /**
   * toString is an alias for toStandardLogString
   *
   * @return the standard log string like
   *         <pre>[INFO] - hostname - 2021-07-20T14:45:20.425 - some.package.Class#method line number 20: hello world</pre>
   */
  override def toString: String = toStandardLogString

  /**
   * toHtmlString is to replace the ANSI codes with html tags from the toStandardLogString result
   *
   * @return the html format log
   */
  def toHtmlString: String = {
    toStandardLogString
      .replace(s"$RED$BOLD", """<div class="head critical">""")
      .replace(s"$RED", """<div class="head error">""")
      .replace(s"$YELLOW", """<div class="head warning">""")
      .replace(s"$GREEN", """<div class="head success">""")
      .replace(s"$BOLD", """<div class="head info">""")
      .replace(s"$RESET", """</div><pre>""")
      .pipe(log => (if (log.startsWith("<div")) """<div class="log">""" else """<div class="log"><div class="head">""") + log + "</pre></div>")
  }

  /**
   * toStandardLogString is an alias for toString
   *
   * @return the standard log string like
   *         <pre>[INFO] - hostname - 2021-07-20T14:45:20.425 - some.package.Class#method line number 20: hello world</pre>
   */
  def toStandardLogString: String = {
    val sb = StringBuilder.newBuilder
    hostname match {
      case Some(value) =>
        sb.append(" - ")
        sb.append(value)
      case None =>
    }
    datetime match {
      case Some(value) =>
        sb.append(" - ")
        sb.append(value)
      case None =>
    }
    className match {
      case Some(value) =>
        sb.append(" - ")
        sb.append(value)
      case None =>
    }
    methodName match {
      case Some(value) =>
        sb.append("#")
        sb.append(value)
      case None =>
    }
    fileName match {
      case Some(value) =>
        sb.append("(")
        sb.append(value)
        if (lineNumber.nonEmpty)
          sb.append(":")
        else
          sb.append(")")
      case None =>
    }
    lineNumber match {
      case Some(value) =>
        sb.append(value)
        sb.append(")")
      case None =>
    }
    sb.append(": ")
    prefix match {
      case Some(value) =>
        sb.append(value)
        sb.append(" - ")
      case None =>
    }
    msg match {
      case Some(value) =>
        sb.append(value)
      case None =>
    }
    val content = sb.result()

    val logString = level match {
      case Some(value) => value match {
        case LogLevel.TRACE => traceLog_unquote.format(content)
        case LogLevel.INFO => infoLog_unquote.format(content)
        case LogLevel.SUCCESS => successLog_unquote.format(content)
        case LogLevel.WARNING => warningLog_unquote.format(content)
        case LogLevel.ERROR => errorLog_unquote.format(content)
        case LogLevel.CRITICAL => criticalLog_unquote.format(content)
        case _ => throw WrongLogLevelException(s"Unknown log level: $value")
      }
      case None => throw WrongLogLevelException("Empty log level")
    }
    val stackTrace = renderThrowable()
    if (stackTrace.isBlank)
      logString
    else
      logString + "\n" + stackTrace
  }

  private def renderThrowable(): String =
    throwable match {
      case Some(t) =>
        val sw = new StringWriter
        val pw = new PrintWriter(sw)
        try t.printStackTrace(pw)
        catch {
          case _: RuntimeException =>
        }
        pw.flush()
        val stackTrace = sw.toString

        s"""${RED}Exception in thread "${Thread.currentThread().getName}" $stackTrace$RESET"""
      case None => ""
    }

}
