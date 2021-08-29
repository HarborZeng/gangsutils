package cn.tellyouwhat.gangsutils.logger

import cn.tellyouwhat.gangsutils.core.constants.{errorLog_unquote, infoLog_unquote}
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.TapIt
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest._
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.exceptions.NoAliveLoggerException

import scala.reflect.runtime.universe

class GangLogger {

  private val logger2Configuration: Map[SupportedLogDest.Value, LoggerConfiguration] = GangLogger.logger2Configuration

  private[logger] val loggers: Seq[Logger] = {
    logger2Configuration.map {
      case (loggerEnum, configuration) =>
        val rm = universe.runtimeMirror(getClass.getClassLoader)
        val module = rm.staticModule(loggerEnum.toString)
        rm.reflectModule(module).instance.asInstanceOf[LoggerCompanion].apply(configuration)
    }
  }.toSeq

  /**
   * 通过参数指定级别的日志
   *
   * @param msg   日志内容
   * @param level 日志级别
   *
   */
  def log(msg: Any, level: LogLevel.Value)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean = {
    (if (enabled != null && enabled.nonEmpty) {
      val unsupportedDests = enabled.map(_.toString).diff(loggers.map(_.getClass.getName))
      if (unsupportedDests.nonEmpty)
        println(errorLog_unquote.format(
          s"Specified log destination ${unsupportedDests.toVector} in ${enabled.map(_.toString).toVector} does not support, supported are ${loggers.map(_.getClass.getName)}"
        ))
      loggers.filter(logger => enabled.exists(_.toString == logger.getClass.getName))
    } else loggers)
      .map(_.log(msg, level)).forall(p => p)
  }

  /**
   * 记录一条跟踪级别的日志
   *
   * @param msg 日志内容
   *
   */
  def trace(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean = log(msg, LogLevel.TRACE)(enabled)

  /**
   * 记录一条信息级别的日志
   *
   * @param msg 日志内容
   *
   */
  def info(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean = log(msg, LogLevel.INFO)(enabled)

  /**
   * 记录一条成功级别的日志
   *
   * @param msg 日志内容
   *
   */
  def success(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean = log(msg, LogLevel.SUCCESS)(enabled)

  /**
   * 记录一条警告级别的日志
   *
   * @param msg 日志内容
   *
   */
  def warning(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean = log(msg, LogLevel.WARNING)(enabled)

  /**
   * 记录一条错误级别的日志
   *
   * @param msg 日志内容
   *
   */
  def error(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean = log(msg, LogLevel.ERROR)(enabled)

  /**
   * 记录一条致命级别的日志
   *
   * @param msg 日志内容
   *
   */
  def critical(msg: Any, throwable: Throwable = null)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean = {
    if (msg == null) false
    else {
      val msgStr = msg.toString
      log(if (throwable != null) s"$msgStr，message is ${throwable.getMessage}" else msgStr, LogLevel.CRITICAL)(enabled)
    }
  }

}

object GangLogger {

  private var logger2Configuration: Map[SupportedLogDest.Value, LoggerConfiguration] = _

  def setLoggerAndConfiguration(m: Map[SupportedLogDest.Value, LoggerConfiguration]): Unit = {
    if (m == null)
      throw new IllegalArgumentException("null m: Map[SupportedLogDest.Value, LoggerConfiguration]")
    if (m.isEmpty)
      throw new IllegalArgumentException("empty m: Map[SupportedLogDest.Value, LoggerConfiguration]")
    logger2Configuration = m
  }

  def apply(): GangLogger = {
    if (logger2Configuration == null) {
      logger2Configuration = Map(PRINTLN_LOGGER -> LoggerConfiguration())
    }
    new GangLogger() |! (l => _logger = Some(l))
  }

  def apply(isDTEnabled: Boolean = true,
            isTraceEnabled: Boolean = false,
            isHostnameEnabled: Boolean = true,
            logPrefix: Option[String] = None,
            logLevel: LogLevel.Value = LogLevel.TRACE): GangLogger = {
    if (logger2Configuration == null) {
      logger2Configuration = Map(PRINTLN_LOGGER -> LoggerConfiguration(isDTEnabled, isTraceEnabled, isHostnameEnabled, logPrefix, logLevel))
    }
    apply()
  }

  private[logger] var _logger: Option[GangLogger] = None

  /**
   * 清除单例 Logger 对象
   */
  def killLogger(): Unit = _logger = None

  def getLogger: GangLogger = _logger match {
    case Some(logger) => logger
    case None =>
      apply() |! (l => _logger = Some(l)) |! (_ => println(infoLog_unquote.format(
        NoAliveLoggerException(I18N.getRB.getString("getLogger.NoAliveLogger"))
      )))
  }

  def clearLogger2Configuration(): Unit = logger2Configuration = null
}
