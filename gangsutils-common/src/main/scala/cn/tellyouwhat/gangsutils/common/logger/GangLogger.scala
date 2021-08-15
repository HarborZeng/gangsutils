package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.exceptions.NoAliveLoggerException
import cn.tellyouwhat.gangsutils.common.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.common.logger.SupportedLogDest.{PRINTLN_LOGGER, WOA_WEBHOOK_LOGGER}


/**
 * BaseLogger 的具体实现，混入了 PrintlnLogger 和 WoaWebhookLogger
 */
protected class GangLogger(
                  override val isDTEnabled: Boolean = GangLogger.isDTEnabled,
                  override val isTraceEnabled: Boolean = GangLogger.isTraceEnabled,
                  override implicit val defaultLogDest: Seq[SupportedLogDest.Value] = GangLogger.defaultLogDest,
                  override val logsLevels: Array[LogLevel.Value] = GangLogger.logsLevels,
                  override val logPrefix: String = GangLogger.logPrefix,
                ) extends PrintlnLogger with WoaWebhookLogger {


  override def log(msg: String, level: LogLevel.Value)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Unit = {
    if (enabled.contains(PRINTLN_LOGGER) && level >= logsLevels(PRINTLN_LOGGER.id))
      super[PrintlnLogger].doTheLogAction(msg, level)
    if (enabled.contains(WOA_WEBHOOK_LOGGER) && level >= logsLevels(WOA_WEBHOOK_LOGGER.id))
      super[WoaWebhookLogger].doTheLogAction(msg, level)
  }

}

object GangLogger {

  /**
   * 创建一个新的 GangLogger 实例
   *
   * @return 一个新的 GangLogger 实例
   */
  def apply(): GangLogger = new GangLogger() |! (l => _logger = Some(l))

  def apply(
             isDTEnabled: Boolean = isDTEnabled,
             isTraceEnabled: Boolean = isTraceEnabled,
             defaultLogDest: Seq[SupportedLogDest.Value] = defaultLogDest,
             logsLevels: Array[LogLevel.Value] = logsLevels,
             logPrefix: String = logPrefix
           ): GangLogger =
    new GangLogger(isDTEnabled, isTraceEnabled, defaultLogDest, logsLevels, logPrefix) |! (l => _logger = Some(l))

  def getLogger: BaseLogger = {
    _logger match {
      case Some(logger) => logger
      case None => apply() |! (logger => logger.warning(NoAliveLoggerException("logger is not initialized yet, initialize a default GangLogger for you")))
    }
  }

  def killLogger(): Unit = _logger = None

  private[logger] var _logger: Option[BaseLogger] = None
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

  private var logPrefix: String = ""

  def resetLoggerConfig(): Unit = {
    isDTEnabled = true
    isTraceEnabled = false
    defaultLogDest = Seq(PRINTLN_LOGGER)
    logsLevels = Array.fill(SupportedLogDest.values.size)(LogLevel.TRACE)
    logPrefix = ""
  }
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
    (SupportedLogDest.values.map(_ -> LogLevel.TRACE).toMap ++ levels).values.toArray |> setLogsLevels
  }

  def setLogPrefix(prefix: String): Unit = logPrefix = prefix

  def clearLogPrefix(): Unit = logPrefix = ""

}
