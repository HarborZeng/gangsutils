package cn.tellyouwhat.gangsutils.logger.cc

import cn.tellyouwhat.gangsutils.core.constants._
import cn.tellyouwhat.gangsutils.logger.LogLevel
import cn.tellyouwhat.gangsutils.logger.exceptions.WrongLogLevelException

import java.time.LocalDateTime

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

  override def toString: String = toStandardLogString
}
