package cn.tellyouwhat.gangsutils.logger

import cn.tellyouwhat.gangsutils.core.constants.{errorLog_unquote, infoLog_unquote}
import cn.tellyouwhat.gangsutils.core.exceptions.GangException
import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.TapIt
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest._
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.exceptions.NoAliveLoggerException

import java.util.Objects
import scala.language.{implicitConversions, postfixOps}
import scala.reflect.runtime.universe

/**
 * GangLogger is a logger stack tool to store multiple loggers, and iteratively perform the log write action.
 *
 * Loggers are initialized using scala reflection.
 */
class GangLogger {

  /**
   * a seq of loggers initialized using scala reflection
   */
  val loggers: Seq[Logger] = {
    GangLogger.logger2ConfigurationAndInitBlock match {
      case Some(v) => v.map {
        case (loggerEnum, (configuration, initBlock)) =>
          initBlock()
          val rm = universe.runtimeMirror(getClass.getClassLoader)
          val module = rm.staticModule(loggerEnum.toString)
          rm.reflectModule(module).instance.asInstanceOf[LoggerCompanion].apply(configuration)
      }
      case None => throw GangException("GangLogger.logger2ConfigurationAndInitBlock is None")
    }
  }

  /**
   * 通过参数指定级别的日志
   *
   * @param msg     日志内容
   * @param level   日志级别
   * @param enabled subset of logger2Configuration.keySet()
   *
   */
  def log(msg: Any, optionThrowable: Option[Throwable], level: LogLevel.Value)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean = {
    // if the enabled parameter is not null nor empty, only those who occurred in both enabled parameter and loggers will perform the log action.
    (if (enabled != null && enabled.nonEmpty) {
      val unsupportedDests = enabled.map(_.toString).diff(loggers.map(_.getClass.getName))
      if (unsupportedDests.nonEmpty)
        println(errorLog_unquote.format(
          s": Specified log destination ${unsupportedDests.toVector} in ${enabled.map(_.toString).toVector} does not support, supported are ${loggers.map(_.getClass.getName)}"
        ))
      loggers.filter(logger => enabled.exists(_.toString == logger.getClass.getName))
    } else loggers)
      .filter(level >= _.loggerConfig.logLevel)
      .map(_.log(msg, optionThrowable, level)).forall(p => p)
  }

  /**
   * 记录一条跟踪级别的日志
   *
   * @param msg     日志内容
   * @param enabled subset of logger2Configuration.keySet()
   *
   */
  def trace(msg: Any, throwable: Throwable = null)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean =
    log(msg, throwable match {
      case null => None
      case t => Some(t)
    }, LogLevel.TRACE)(enabled)

  /**
   * 记录一条信息级别的日志
   *
   * @param msg     日志内容
   * @param enabled subset of logger2Configuration.keySet()
   *
   */
  def info(msg: Any, throwable: Throwable = null)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean =
    log(msg, throwable match {
      case null => None
      case t => Some(t)
    }, LogLevel.INFO)(enabled)

  /**
   * 记录一条成功级别的日志
   *
   * @param msg     日志内容
   * @param enabled subset of logger2Configuration.keySet()
   *
   */
  def success(msg: Any, throwable: Throwable = null)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean =
    log(msg, throwable match {
      case null => None
      case t => Some(t)
    }, LogLevel.SUCCESS)(enabled)

  /**
   * 记录一条警告级别的日志
   *
   * @param msg     日志内容
   * @param enabled subset of logger2Configuration.keySet()
   *
   */
  def warning(msg: Any, throwable: Throwable = null)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean =
    log(msg, throwable match {
      case null => None
      case t => Some(t)
    }, LogLevel.WARNING)(enabled)

  /**
   * 记录一条错误级别的日志
   *
   * @param msg     日志内容
   * @param enabled subset of logger2Configuration.keySet()
   *
   */
  def error(msg: Any, throwable: Throwable = null)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean =
    log(msg, throwable match {
      case null => None
      case t => Some(t)
    }, LogLevel.ERROR)(enabled)

  /**
   * 记录一条致命级别的日志
   *
   * @param msg     日志内容
   * @param enabled subset of logger2Configuration.keySet()
   *
   */
  def critical(msg: Any, throwable: Throwable = null)(implicit enabled: Seq[SupportedLogDest.Value] = Nil): Boolean =
    log(msg, throwable match {
      case null => None
      case t => Some(t)
    }, LogLevel.CRITICAL)(enabled)
}

/**
 * GangLogger is a logger stack tool to store multiple loggers, and iteratively perform the log write action.
 *
 * GangLogger object can statically execute `setLoggerAndConfiguration`, `killLogger`, `getLogger`, `clearLogger2Configuration` and etc.
 *
 * like:
 *
 * <pre>
 * import cn.tellyouwhat.gangsutils.logger.GangLogger
 * import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.{LOCAL_HTML_LOGGER, PRINTLN_LOGGER}
 * import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
 * import cn.tellyouwhat.gangsutils.logger.dest.fs.LocalHtmlLogger
 *
 * LocalHtmlLogger.setLogSavePath("logs/ground.html")
 * GangLogger.setLoggerAndConfiguration(Map(
 * PRINTLN_LOGGER -> LoggerConfiguration(isDTEnabled = true, isTraceEnabled = true, isHostnameEnabled = true, logPrefix = Some("prefix")),
 * LOCAL_HTML_LOGGER -> LoggerConfiguration(isDTEnabled = true, isTraceEnabled = true, isHostnameEnabled = true, logPrefix = Some("prefix")),
 * ))
 * val logger: GangLogger = GangLogger()
 * </pre>
 */
object GangLogger {

  /**
   * the specified map of log destination enumeration to [[LoggerConfiguration]]
   */
  private var logger2ConfigurationAndInitBlock: Option[Seq[(SupportedLogDest.Value, (LoggerConfiguration, () => Unit))]] = None

  /**
   * the stored _logger singleton
   */
  @volatile private[logger] var _logger: Option[GangLogger] = None

  /**
   * set the log destination to [[LoggerConfiguration]] mappings
   *
   * @param m the log destination to [[LoggerConfiguration]] mappings
   */
  def setLoggerAndConfiguration(m: Map[SupportedLogDest.Value, LoggerConfiguration]): Unit = {
    Objects.requireNonNull(m)
    setLoggerAndConfiguration(m.toSeq)
  }

  /**
   * set the log destination to LoggerConfiguration sequence
   *
   * @param s the log destination to LoggerConfiguration sequence, duplicate destinations are supported
   */
  def setLoggerAndConfiguration(s: Seq[(SupportedLogDest.Value, LoggerConfiguration)]): Unit = {
    Objects.requireNonNull(s)
    if (s.isEmpty)
      throw new IllegalArgumentException("empty parameter")
    logger2ConfigurationAndInitBlock = Some(s.map(o => (o._1, (o._2, () => {}))))
  }

  /**
   * set the log destination to (LoggerConfiguration, InitBlock) sequence
   *
   * @param s the log destination to LoggerConfiguration sequence, duplicate destinations are supported
   */
  def setLoggerAndConfigurationAndInitBlock(s: Seq[(SupportedLogDest.Value, (LoggerConfiguration, () => Unit))]): Unit = {
    Objects.requireNonNull(s)
    if (s.isEmpty)
      throw new IllegalArgumentException("empty parameter")
    logger2ConfigurationAndInitBlock = Some(s)
  }

  /**
   * if you fill these parameters without executing `setLoggerAndConfiguration`, a map of [[PRINTLN_LOGGER]] -> [[LoggerConfiguration]] will be created with the parameters you filled.
   *
   * if you fill these parameters with `setLoggerAndConfiguration` executed, the specified loggers will be create.
   *
   * @param isDTEnabled       is datetime enabled, if `setLoggerAndConfiguration` has already been executed, this parameter will be ignored
   * @param isTraceEnabled    is trace enabled, if `setLoggerAndConfiguration` has already been executed, this parameter will be ignored
   * @param isHostnameEnabled is hostname enabled, if `setLoggerAndConfiguration` has already been executed, this parameter will be ignored
   * @param logPrefix         option of log prefix string, if `setLoggerAndConfiguration` has already been executed, this parameter will be ignored
   * @param logLevel          one of [[LogLevel]] log level, if `setLoggerAndConfiguration` has already been executed, this parameter will be ignored
   * @return the expected [[GangLogger]] instance
   */
  def apply(isDTEnabled: Boolean = true,
            isTraceEnabled: Boolean = false,
            isHostnameEnabled: Boolean = true,
            logPrefix: Option[String] = None,
            logLevel: LogLevel.Value = LogLevel.TRACE): GangLogger = {
    if (logger2ConfigurationAndInitBlock.isEmpty) {
      Seq(PRINTLN_LOGGER -> LoggerConfiguration(isDTEnabled, isTraceEnabled, isHostnameEnabled, logPrefix, logLevel)) |! setLoggerAndConfiguration
    }
    apply()
  }

  /**
   * 清除单例 [[GangLogger]] 对象
   */
  def killLogger(): Unit = _logger = None

  /**
   * get [[GangLogger]] instance if the underlying _logger is not None or apply a new one if it is
   *
   * @return the [[GangLogger]] instance
   */
  def getLogger: GangLogger = _logger match {
    case Some(logger) => logger
    case None =>
      synchronized {
        if (_logger.isEmpty) {
          apply() |! (_ => println(infoLog_unquote.format(
            NoAliveLoggerException(I18N.getRB.getString("getLogger.NoAliveLogger"))
          )))
        } else {
          getLogger
        }
      }
  }

  /**
   * if you have not execute `setLoggerAndConfiguration`, a map of [[PRINTLN_LOGGER]] -> [[LoggerConfiguration]] will be created with the default values.
   *
   * if you have executed `setLoggerAndConfiguration`, the specified loggers will be create.
   *
   * @return the expected [[GangLogger]] instance
   */
  def apply(): GangLogger = {
    if (logger2ConfigurationAndInitBlock.isEmpty) {
      Seq(PRINTLN_LOGGER -> LoggerConfiguration()) |! setLoggerAndConfiguration
    }
    new GangLogger() |! (l => _logger = Some(l))
  }

  def clearLogger2Configuration(): Unit = logger2ConfigurationAndInitBlock = None

  /**
   * helps to call by-name parameter without the thunk syntax
   */
  implicit def blockToThunk(bl: => Unit): () => Unit = () => bl

}
