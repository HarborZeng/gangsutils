package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.helper.I18N.getRB
import cn.tellyouwhat.gangsutils.common.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.common.logger.SupportedLogDest.PRINTLN_LOGGER

import java.net.InetAddress
import java.time.LocalDateTime

/**
 * 日志基础特质
 */
trait BaseLogger {

  /**
   * 是否在日志中启用时间
   */
  private[logger] val isDTEnabled: Boolean = true

  /**
   * 是否在日志中启用跟踪（包名类名方法名行号）字段
   */
  private[logger] val isTraceEnabled: Boolean = false

  /**
   * 默认的日志输出目的地
   */
  private[logger] implicit val defaultLogDest: Seq[SupportedLogDest.Value] = Seq(PRINTLN_LOGGER)

  /**
   * 默认的不同的日志输出目的地的级别
   */
  private[logger] val logsLevels: Array[LogLevel.Value] = Array.fill(SupportedLogDest.values.size)(LogLevel.TRACE)

  /**
   * 每条日志的前缀
   */
  private[logger] val logPrefix: Option[String] = None

  private[logger] lazy val hostname: String = InetAddress.getLocalHost.getHostName

  private[logger] val isHostnameEnabled: Boolean = true


  /**
   * 构建日志文本
   *
   * @param msg 日志内容
   * @return
   */
  protected def buildLog(msg: String, level: LogLevel.Value): OneLog = {
    val (className, methodName, lineNumber) = if (isTraceEnabled) {
      val stackTraceElements = Thread.currentThread().getStackTrace
      val slicedElements = stackTraceElements.drop(stackTraceElements
        .lastIndexWhere(_.getClassName.startsWith("cn.tellyouwhat.gangsutils.common.logger")) + 1
      ).filterNot(e => e.getClassName.startsWith("sun.") ||
        e.getClassName.startsWith("java.") ||
        e.getClassName.startsWith("scala.") ||
        e.getClassName.startsWith("cn.tellyouwhat.gangsutils.common.gangfunctions"))
      val theTrace = slicedElements(0)
      val className = theTrace.getClassName
      val methodName = theTrace.getMethodName
      val lineNumber = s"${getRB.getString("nth_line").format(theTrace.getLineNumber)}"
      (Some(className), Some(methodName), Some(lineNumber))
    } else {
      (None, None, None)
    }
    OneLog(
      level = Some(level),
      hostname = if (isHostnameEnabled) Some(hostname) else None,
      datetime = if (isDTEnabled) Some(LocalDateTime.now()) else None,
      className = className,
      methodName = methodName,
      lineNumber = lineNumber,
      prefix = logPrefix,
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
   * 通过参数指定级别的日志
   *
   * @param msg     日志内容
   * @param level   日志级别
   * @param enabled 启用的日志目的地
   */
  def log(msg: Any, level: LogLevel.Value)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Boolean

  /**
   * 记录一条跟踪级别的日志
   *
   * @param msg     日志内容
   * @param enabled 启用的日志目的地
   */
  def trace(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Boolean = log(msg, LogLevel.TRACE)(enabled)

  /**
   * 记录一条信息级别的日志
   *
   * @param msg     日志内容
   * @param enabled 启用的日志目的地
   */
  def info(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Boolean = log(msg, LogLevel.INFO)(enabled)

  /**
   * 记录一条成功级别的日志
   *
   * @param msg     日志内容
   * @param enabled 启用的日志目的地
   */
  def success(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Boolean = log(msg, LogLevel.SUCCESS)(enabled)

  /**
   * 记录一条警告级别的日志
   *
   * @param msg     日志内容
   * @param enabled 启用的日志目的地
   */
  def warning(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Boolean = log(msg, LogLevel.WARNING)(enabled)

  /**
   * 记录一条错误级别的日志
   *
   * @param msg     日志内容
   * @param enabled 启用的日志目的地
   */
  def error(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Boolean = log(msg, LogLevel.ERROR)(enabled)

  /**
   * 记录一条致命级别的日志
   *
   * @param msg     日志内容
   * @param enabled 启用的日志目的地
   */
  def critical(msg: Any, throwable: Throwable = null)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Boolean = {
    if (msg == null) false
    else msg.toString |>
      (msgStr => log(if (throwable != null) s"$msgStr，exception.getMessage: ${throwable.getMessage}" else msgStr, LogLevel.CRITICAL)(enabled))
  }

}
