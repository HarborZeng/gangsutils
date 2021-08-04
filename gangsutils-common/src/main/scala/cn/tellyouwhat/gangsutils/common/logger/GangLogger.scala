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

  /**
   * 创建一个新的 GangLogger 实例
   *
   * @return 一个新的 GangLogger 实例
   */
  def apply() = new GangLogger()

  /**
   * 是否在日志中启用时间
   */
  private var isDTEnabled: Boolean = true

  /**
   * 是否在日志中启用跟踪（包名类名方法名行号）字段
   */
  private var isTraceEnabled: Boolean = false

  /**
   * 默认的日志输出目的地
   */
  private var defaultLogDest: Seq[SupportedLogDest.Value] = Seq(PRINTLN_LOGGER)

  /**
   * 默认的不同的日志输出目的地的级别
   */
  private var logsLevels: Array[LogLevel.Value] = Array.fill(SupportedLogDest.values.size)(LogLevel.TRACE)

  /**
   * 关闭日志中的日期时间
   */
  def disableDateTime(): Unit =
    isDTEnabled = false

  /**
   * 启用日志中的日期时间
   */
  def enableDateTime(): Unit =
    isDTEnabled = true

  /**
   * 关闭日志中的包名类名方法名行号
   */
  def disableTrace(): Unit =
    isTraceEnabled = false

  /**
   * 启用日志中的包名类名方法名行号
   *
   * 注意：如果有短时大量日志输出，启用此功能将会影响程序性能
   */
  def enableTrace(): Unit =
    isTraceEnabled = true

  /**
   * 设置默认的日志输出目的地
   *
   * @param destination 日志输出目的地
   */
  def setDefaultLogDest(destination: Seq[SupportedLogDest.Value]): Unit =
    defaultLogDest = destination

  /**
   * 设置日志级别
   *
   * @param levels 每个级别对应所支持的每一个日志
   */
  def setLogsLevels(levels: Array[LogLevel.Value]): Unit = {
    if (levels.length != SupportedLogDest.values.size) {
      throw new IllegalArgumentException(s"logsLevels 数量和 SupportedLogDest 所支持的数量不符：" +
        s"levels.length ${levels.length}, SupportedLogDest.values.size ${SupportedLogDest.values.size}")
    }
    logsLevels = levels
  }

  /**
   * 设置日志级别
   *
   * @param levels 每个级别对应所支持的每一个日志，如 `Map(PRINTLN_LOGGER -> LogLevel.TRACE)`
   */
  def setLogsLevels(levels: Map[SupportedLogDest.Value, LogLevel.Value]): Unit = {
    if (levels == null || levels.isEmpty) {
      throw new IllegalArgumentException(s"levels map 不合法：$levels")
    }
    // ValueSet object is a sorted set by design
    (SupportedLogDest.values.map(_ -> LogLevel.TRACE).toMap ++ levels).values.toArray |!! setLogsLevels
  }

}
