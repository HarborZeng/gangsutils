package cn.tellyouwhat.gangsutils.logger.cc

import cn.tellyouwhat.gangsutils.core.constants._
import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.logger.LogLevel
import cn.tellyouwhat.gangsutils.logger.exceptions.WrongLogLevelException

import java.time.LocalDateTime
import scala.io.AnsiColor._

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

  override def toString: String = toStandardLogString

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
    val content = sb.result()

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
