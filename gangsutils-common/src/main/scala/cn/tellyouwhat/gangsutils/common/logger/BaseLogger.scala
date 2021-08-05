package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.exceptions.GangException
import cn.tellyouwhat.gangsutils.common.gangfunctions.chainSideEffect

import java.time.LocalDateTime

/**
 * 日志基础特质
 */
trait BaseLogger {

  /**
   * 是否在日志中启用时间
   */
  protected val isDTEnabled: Boolean = true

  /**
   * 是否在日志中启用跟踪（包名类名方法名行号）字段
   */
  protected val isTraceEnabled: Boolean = false

  /**
   * 默认的日志输出目的地
   */
  protected implicit val defaultLogDest: Seq[SupportedLogDest.Value] = null

  /**
   * 默认的不同的日志输出目的地的级别
   */
  protected val logsLevels: Array[LogLevel.Value] = Array.fill(SupportedLogDest.values.size)(LogLevel.TRACE)

  /**
   * 构建日志文本
   *
   * @param msg   日志内容
   * @param level 日志级别
   * @return
   */
  protected def buildLogContent(msg: String, level: LogLevel.Value): String =
    (if (isTraceEnabled) {
      val stackTraceElements = Thread.currentThread().getStackTrace
      val slicedElements = stackTraceElements.drop(stackTraceElements
        .lastIndexWhere(_.getClassName.startsWith("cn.tellyouwhat.gangsutils.common.logger")) + 1
      ).filterNot(e => e.getClassName.startsWith("sun.") || e.getClassName.startsWith("java.") ||
        e.getClassName.startsWith("scala.") || (e.getClassName == "cn.tellyouwhat.gangsutils.common.gangfunctions$" && e.getMethodName == "timeit"))
      val theTrace = slicedElements(0)
      s" - ${theTrace.getClassName}#${theTrace.getMethodName}第${theTrace.getLineNumber}行"
    } else {
      ""
    }) |!! (traceStr => s"【$level】${if (isDTEnabled) s" - ${LocalDateTime.now().toString}" else ""}$traceStr: $msg")

  /**
   * 真正去输出一条日志
   *
   * @param msg   日志内容
   * @param level 日志级别
   */
  protected def doTheLogAction(msg: String, level: LogLevel.Value): Unit

  /**
   * 通过参数指定级别的日志
   *
   * @param msg     日志内容
   * @param level   日志级别
   * @param enabled 启用的日志目的地
   */
  def log(msg: String, level: LogLevel.Value)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit

  /**
   * 记录一条跟踪级别的日志
   *
   * @param msg     日志内容
   * @param enabled 启用的日志目的地
   */
  def trace(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit

  /**
   * 记录一条信息级别的日志
   *
   * @param msg     日志内容
   * @param enabled 启用的日志目的地
   */
  def info(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit

  /**
   * 记录一条成功级别的日志
   *
   * @param msg     日志内容
   * @param enabled 启用的日志目的地
   */
  def success(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit

  /**
   * 记录一条警告级别的日志
   *
   * @param msg     日志内容
   * @param enabled 启用的日志目的地
   */
  def warning(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit

  /**
   * 记录一条错误级别的日志
   *
   * @param msg     日志内容
   * @param enabled 启用的日志目的地
   */
  def error(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit

  /**
   * 记录一条致命级别的日志
   *
   * @param msg     日志内容
   * @param enabled 启用的日志目的地
   */
  @throws[GangException]
  def critical(msg: Any, throwable: Throwable = null)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit
}
