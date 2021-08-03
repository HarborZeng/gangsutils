package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.exceptions.GangException
import cn.tellyouwhat.gangsutils.common.gangfunctions.chainSideEffect
import cn.tellyouwhat.gangsutils.common.logger.SupportedLogDest.{PRINTLN_LOGGER, WOA_WEBHOOK_LOGGER}


/**
 * BaseLogger 的具体实现，混入了 PrintlnLogger 和 WoaWebhookLogger
 */
class GangLogger extends PrintlnLogger with WoaWebhookLogger {

  override val isDTEnabled: Boolean = GangLogger.isDTEnabled
  override val isTraceEnabled: Boolean = GangLogger.isTraceEnabled
  override implicit val defaultLogDest: Seq[SupportedLogDest.Value] = GangLogger.defaultLogDest
  override val logsLevels: Array[LogLevel.Value] = GangLogger.logsLevels

  private def log(msg: String, level: LogLevel.Value)(implicit enabled: Seq[SupportedLogDest.Value]): Unit = {
    if (enabled.contains(PRINTLN_LOGGER) && level >= logsLevels(PRINTLN_LOGGER.id))
      super[PrintlnLogger].doTheLogAction(msg, level)
    if (enabled.contains(WOA_WEBHOOK_LOGGER) && level >= logsLevels(WOA_WEBHOOK_LOGGER.id))
      super[WoaWebhookLogger].doTheLogAction(msg, level)
  }


  override def info(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit = log(msg.toString, LogLevel.INFO)(enabled)

  override def error(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit = log(msg.toString, LogLevel.ERROR)(enabled)

  @throws[GangException]
  override def critical(msg: Any, throwable: Throwable = null)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit =
    msg.toString |!
      (msgStr => log(if (throwable != null) s"$msgStr，exception.getMessage: ${throwable.getMessage}" else msgStr, LogLevel.CRITICAL)(enabled)) |!
      (msgStr => throw GangException(s"出现致命错误: $msgStr", throwable))

  override def warning(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit = log(msg.toString, LogLevel.WARNING)(enabled)

  override def success(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit = log(msg.toString, LogLevel.SUCCESS)(enabled)

  override def trace(msg: Any)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit = log(msg.toString, LogLevel.TRACE)(enabled)

}

object GangLogger {

  def apply() = new GangLogger()

  private var isDTEnabled: Boolean = true
  private var isTraceEnabled: Boolean = true
  private var defaultLogDest: Seq[SupportedLogDest.Value] = Seq(PRINTLN_LOGGER)
  private var logsLevels: Array[LogLevel.Value] = Array.fill(SupportedLogDest.values.size)(LogLevel.TRACE)

  def disableDateTime(): Unit =
    isDTEnabled = false

  def enableDateTime(): Unit =
    isDTEnabled = true

  def disableTrace(): Unit =
    isTraceEnabled = false

  def enableTrace(): Unit =
    isTraceEnabled = true

  def setDefaultLogDest(destination: Seq[SupportedLogDest.Value]): Unit =
    defaultLogDest = destination

  def setLogsLevels(levels: Array[LogLevel.Value]): Unit = {
    if (levels.length != SupportedLogDest.values.size) {
      throw new IllegalArgumentException(s"logsLevels 数量和 SupportedLogDest 所支持的数量不符：" +
        s"levels.length ${levels.length}, SupportedLogDest.values.size ${SupportedLogDest.values.size}")
    }
    logsLevels = levels
  }

  def setLogsLevels(levels: Map[SupportedLogDest.Value, LogLevel.Value]): Unit = {
    if (levels == null || levels.isEmpty) {
      throw new IllegalArgumentException(s"levels map 不合法：$levels")
    }
    // ValueSet object is a sorted set by design
    (SupportedLogDest.values.map(_ -> LogLevel.TRACE).toMap ++ levels).values.toArray |!! setLogsLevels
  }

}
