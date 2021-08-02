package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.gangfunctions.chainSideEffect

import java.time.LocalDateTime

trait BaseLogger {

  protected val isDTEnabled: Boolean = true
  protected val isTraceEnabled: Boolean = true
  protected implicit val defaultLogDest: Seq[SupportedLogDest.Value] = null
  protected val logsLevels: Array[LogLevel.Value] = Array.fill(SupportedLogDest.values.size)(LogLevel.TRACE)


  protected def buildLogContent(msg: String, level: LogLevel.Value, dt: Boolean, trace: Boolean): String =
    (if (trace) {
      val stackTraceElements = Thread.currentThread().getStackTrace
      val slicedElements = stackTraceElements.drop(stackTraceElements
        .lastIndexWhere(_.getClassName.startsWith("cn.tellyouwhat.gangsutils.common.logger")) + 1
      ).filterNot(e => e.getClassName.startsWith("sun.") || e.getClassName.startsWith("java.") ||
        e.getClassName.startsWith("scala.") || (e.getClassName == "cn.tellyouwhat.gangsutils.common.gangfunctions$" && e.getMethodName == "timeit"))
      val theTrace = slicedElements(0)
      s" - ${theTrace.getClassName}#${theTrace.getMethodName}第${theTrace.getLineNumber}行"
    } else {
      ""
    }) |!! (traceStr => s"【${level}】${if (dt) s" - ${LocalDateTime.now().toString}" else ""}${traceStr}: ${msg}")

  protected def doTheLogAction(msg: String, level: LogLevel.Value, dt: Boolean, trace: Boolean): Unit

  def trace(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value]): Unit

  def info(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value]): Unit

  def success(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value]): Unit

  def warning(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value]): Unit

  def error(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value]): Unit

  def critical(msg: Any, throwable: Throwable)(implicit enabled: Seq[SupportedLogDest.Value]): Unit
}
