package cn.tellyouwhat.gangsutils.logger

import cn.tellyouwhat.gangsutils.core.exceptions.GangException
import cn.tellyouwhat.gangsutils.core.helper.I18N.getRB
import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.logger.cc.{LoggerConfiguration, OneLog}

import java.net.InetAddress
import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
 * 日志基础特质
 */
trait Logger {

  /**
   * lazy value of hostname
   */
  lazy val hostname: String = InetAddress.getLocalHost.getHostName

  /**
   * LoggerConfiguration for the logger to stylish the log
   */
  val loggerConfig: LoggerConfiguration = null

  /**
   * 记录一条跟踪级别的日志
   *
   * @param msg 日志内容
   *
   */
  def trace(msg: Any): Boolean = log(msg, LogLevel.TRACE)

  /**
   * 记录一条信息级别的日志
   *
   * @param msg 日志内容
   *
   */
  def info(msg: Any): Boolean = log(msg, LogLevel.INFO)

  /**
   * 通过参数指定级别的日志
   *
   * @param msg   日志内容
   * @param level 日志级别
   *
   */
  def log(msg: Any, level: LogLevel.Value): Boolean = {
    checkPrerequisite()
    if (level >= loggerConfig.logLevel) {
      if (loggerConfig.async) {
        val f = doTheLogActionAsync(msg.toString, level)
        f.onComplete {
          case Failure(exception) => exception.printStackTrace()
          case Success(_) =>
        }
        // async log always return true
        true
      } else
        doTheLogAction(msg.toString, level)
    } else false
  }

  /**
   * check prerequisite before perform the real log action
   */
  protected def checkPrerequisite(): Unit = {
    if (loggerConfig == null)
      throw GangException("loggerConfig is null")
  }

  /**
   * 记录一条成功级别的日志
   *
   * @param msg 日志内容
   *
   */
  def success(msg: Any): Boolean = log(msg, LogLevel.SUCCESS)

  /**
   * 记录一条警告级别的日志
   *
   * @param msg 日志内容
   *
   */
  def warning(msg: Any): Boolean = log(msg, LogLevel.WARNING)

  /**
   * 记录一条错误级别的日志
   *
   * @param msg 日志内容
   *
   */
  def error(msg: Any): Boolean = log(msg, LogLevel.ERROR)

  /**
   * 记录一条致命级别的日志
   *
   * @param msg 日志内容
   *
   */
  def critical(msg: Any, throwable: Throwable = null): Boolean = {
    if (msg == null) false
    else msg.toString |>
      (msgStr => log(if (throwable != null) s"$msgStr，message is ${throwable.getMessage}" else msgStr, LogLevel.CRITICAL))
  }

  /**
   * 构建日志文本
   *
   * @param msg 日志内容
   * @return
   */
  protected def buildLog(msg: String, level: LogLevel.Value): OneLog = {
    val (className, methodName, lineNumber) = if (loggerConfig.isTraceEnabled) {
      val stackTraceElements = Thread.currentThread().getStackTrace
      val slicedElements = stackTraceElements
        .filterNot(e => e.getClassName.startsWith("sun.") ||
          e.getClassName.startsWith("java.") ||
          e.getClassName.startsWith("scala.") ||
          e.getClassName.startsWith("cn.tellyouwhat.gangsutils"))
      if (slicedElements.isEmpty)
        (None, None, None)
      else {
        val theTrace = slicedElements(0)
        val className = theTrace.getClassName
        val methodName = theTrace.getMethodName
        val lineNumber = s"${getRB.getString("nth_line").format(theTrace.getLineNumber)}"
        (Some(className), Some(methodName), Some(lineNumber))
      }
    } else {
      (None, None, None)
    }
    OneLog(
      level = Some(level),
      hostname = if (loggerConfig.isHostnameEnabled) Some(hostname) else None,
      datetime = if (loggerConfig.isDTEnabled) Some(LocalDateTime.now()) else None,
      className = className,
      methodName = methodName,
      lineNumber = lineNumber,
      prefix = loggerConfig.logPrefix,
      msg = Some(msg)
    )
  }

  /**
   * 真正去输出一条日志
   *
   * @param msg   日志内容
   * @param level 日志级别
   */
  protected def doTheLogAction(msg: String, level: LogLevel.Value): Boolean

  /**
   * 异步地真正去输出一条日志
   *
   * @param msg   日志内容
   * @param level 日志级别
   */
  protected def doTheLogActionAsync(msg: String, level: LogLevel.Value): Future[Boolean] = Future(doTheLogAction(msg, level))

}

/**
 * trait of Logger companion object
 */
trait LoggerCompanion {

  /**
   * the logger full name
   */
  val loggerName: String = null

  /**
   * the logger configuration
   */
  private[logger] var loggerConfig: Option[LoggerConfiguration] = None

  /**
   * create a default logger if loggerConfig is not None
   *
   * @return a Logger instance
   */
  def apply(): Logger

  /**
   * create a logger with LoggerConfiguration
   *
   * @return a Logger instance
   */
  def apply(c: LoggerConfiguration): Logger = {
    initializeConfiguration(c)
    apply()
  }

  /**
   * set LoggerConfiguration
   */
  def initializeConfiguration(c: LoggerConfiguration): Unit = loggerConfig = Some(c)

  /**
   * set LoggerConfiguration
   */
  def resetConfiguration(): Unit = loggerConfig = None
}
