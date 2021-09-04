package cn.tellyouwhat.gangsutils.logger.cc

import cn.tellyouwhat.gangsutils.core.constants._
import cn.tellyouwhat.gangsutils.core.funcs.escapeQuotationMark
import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.logger.LogLevel
import cn.tellyouwhat.gangsutils.logger.exceptions.WrongLogLevelException
import io.circe.Encoder
import io.circe.syntax._

import java.time.LocalDateTime
import scala.io.AnsiColor._

/**
 * OneLog case class is for storing a real log, including level, hostname, datetime, className, methodName, lineNumber, prefix and msg
 *
 * @param level      option log level of {@link LogLevel}
 * @param hostname   option hostname of String
 * @param datetime   option datetime of {@link LocalDateTime}
 * @param className  option className of String
 * @param methodName option methodName of String
 * @param lineNumber option lineNumber of String
 * @param prefix     option prefix of String
 * @param msg        option msg of String, the real log content
 */
case class OneLog(
                   level: Option[LogLevel.Value],
                   hostname: Option[String],
                   datetime: Option[LocalDateTime],
                   className: Option[String],
                   methodName: Option[String],
                   lineNumber: Option[String],
                   prefix: Option[String],
                   msg: Option[String],
                 ) {

  /**
   * toJsonString can convert this case class to json string without space
   *
   * @return a json string without space
   */
  def toJsonString: String = {
    this.asJson(Encoder.forProduct8(
      "level",
      "hostname",
      "datetime",
      "className",
      "methodName",
      "lineNumber",
      "prefix",
      "msg",
    )(u => (
      u.level.orNull.toString,
      u.hostname,
      u.datetime,
      u.className,
      u.methodName,
      u.lineNumber,
      u.prefix,
      u.msg,
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
    lineNumber match {
      case Some(value) =>
        sb.append(" ")
        sb.append(value)
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
    val content = sb.result() |> escapeQuotationMark

    level match {
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
  }

}
