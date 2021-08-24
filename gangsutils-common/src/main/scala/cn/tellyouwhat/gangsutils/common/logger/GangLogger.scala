package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.exceptions.NoAliveLoggerException
import cn.tellyouwhat.gangsutils.common.helper.I18N
import cn.tellyouwhat.gangsutils.common.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.common.logger.SupportedLogDest.{PRINTLN_LOGGER, QYWX_WEBHOOK_LOGGER, SLACK_WEBHOOK_LOGGER, WOA_WEBHOOK_LOGGER}


/**
 * BaseLogger 的具体实现，混入了 PrintlnLogger 和 WoaWebhookLogger
 */
protected class GangLogger(
                            override val isDTEnabled: Boolean = GangLogger.isDTEnabled,
                            override val isTraceEnabled: Boolean = GangLogger.isTraceEnabled,
                            override implicit val defaultLogDest: Seq[SupportedLogDest.Value] = GangLogger.defaultLogDest,
                            override val logsLevels: Array[LogLevel.Value] = GangLogger.logsLevels,
                            override val logPrefix: String = GangLogger.logPrefix,
                            override val isHostnameEnabled: Boolean = GangLogger.isHostnameEnabled,
                          ) extends PrintlnLogger with WoaWebhookLogger with SlackWebhookLogger with QYWXWebhookLogger {


  override def log(msg: String, level: LogLevel.Value)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Boolean = {
    val logStatus = Array.newBuilder[Boolean]
    if (enabled.contains(PRINTLN_LOGGER) && level >= logsLevels(PRINTLN_LOGGER.id)) {
      val status = super[PrintlnLogger].doTheLogAction(msg, level)
      logStatus += status
    }
    if (enabled.contains(WOA_WEBHOOK_LOGGER) && level >= logsLevels(WOA_WEBHOOK_LOGGER.id)) {
      val status = doTheLogAction4WOA(msg, level)
      logStatus += status
    }
    if (enabled.contains(SLACK_WEBHOOK_LOGGER) && level >= logsLevels(SLACK_WEBHOOK_LOGGER.id)) {
      val status = doTheLogAction4Slack(msg, level)
      logStatus += status
    }
    if (enabled.contains(QYWX_WEBHOOK_LOGGER) && level >= logsLevels(QYWX_WEBHOOK_LOGGER.id)) {
      val status = doTheLogAction4QYWX(msg, level)
      logStatus += status
    }
    logStatus.result().forall(b => b)
  }

  private def doTheLogAction4WOA(msg: String, level: LogLevel.Value): Boolean = {
    super[WoaWebhookLogger].checkPrerequisite()
    super[WoaWebhookLogger].webhookLog(msg, level)
  }

  private def doTheLogAction4Slack(msg: String, level: LogLevel.Value): Boolean = {
    super[SlackWebhookLogger].checkPrerequisite()
    super[SlackWebhookLogger].webhookLog(msg, level)
  }

  private def doTheLogAction4QYWX(msg: String, level: LogLevel.Value): Boolean = {
    super[QYWXWebhookLogger].checkPrerequisite()
    super[QYWXWebhookLogger].webhookLog(msg, level)
  }
}

object GangLogger {

  /**
   * 创建一个新的 GangLogger 实例
   *
   * @return 一个新的 GangLogger 实例
   */
  def apply(): GangLogger = new GangLogger() |! (l => _logger = Some(l))

  /**
   * 创建一个新的 GangLogger 实例
   *
   * @param isDTEnabled       是否在日志中启用时间
   * @param isTraceEnabled    是否在日志中启用跟踪（包名类名方法名行号）字段
   * @param defaultLogDest    默认的日志输出目的地
   * @param logsLevels        默认的不同的日志输出目的地的级别
   * @param logPrefix         每条日志的前缀
   * @param isHostnameEnabled 是否在日志中启用主机名字段
   * @return 一个新的 GangLogger 实例
   */
  def apply(
             isDTEnabled: Boolean = isDTEnabled,
             isTraceEnabled: Boolean = isTraceEnabled,
             defaultLogDest: Seq[SupportedLogDest.Value] = defaultLogDest,
             logsLevels: Array[LogLevel.Value] = logsLevels,
             logPrefix: String = logPrefix,
             isHostnameEnabled: Boolean = isHostnameEnabled
           ): GangLogger =
    new GangLogger(isDTEnabled, isTraceEnabled, defaultLogDest, logsLevels, logPrefix, isHostnameEnabled) |! (l => _logger = Some(l))

  /**
   * 获取 BaseLogger 单例对象
   *
   * @return
   */
  def getLogger: BaseLogger = {
    _logger match {
      case Some(logger) => logger
      case None => apply() |! (logger => logger.warning(NoAliveLoggerException(I18N.getRB.getString("getLogger.NoAliveLogger"))))
    }
  }

  /**
   * 清除单例 BaseLogger 对象
   */
  def killLogger(): Unit = _logger = None

  /**
   * BaseLogger 的单例对象
   */
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
   * 是否在日志中启用主机名字段
   */
  private var isHostnameEnabled: Boolean = true

  /**
   * 默认的日志输出目的地
   */
  private var defaultLogDest: Seq[SupportedLogDest.Value] = Seq(PRINTLN_LOGGER)

  /**
   * 默认的不同的日志输出目的地的级别
   */
  private var logsLevels: Array[LogLevel.Value] = Array.fill(SupportedLogDest.values.size)(LogLevel.TRACE)

  /**
   * 日志前缀，每条日志前都会被加上的前缀
   */
  private var logPrefix: String = ""

  /**
   * 将 GangLogger 伴生对像变量初始化
   */
  def resetLoggerConfig(): Unit = {
    isDTEnabled = true
    isTraceEnabled = false
    defaultLogDest = Seq(PRINTLN_LOGGER)
    logsLevels = Array.fill(SupportedLogDest.values.size)(LogLevel.TRACE)
    logPrefix = ""
    isHostnameEnabled = true
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
   * 关闭日志中的主机名字段
   */
  def disableHostname(): Unit =
    isHostnameEnabled = false

  /**
   * 启用日志中的主机名字段
   */
  def enableHostname(): Unit =
    isHostnameEnabled = true

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
      throw new IllegalArgumentException(I18N.getRB.getString("setLogsLevels.illegalArray").format(levels.length, SupportedLogDest.values.size))
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
      throw new IllegalArgumentException(I18N.getRB.getString("setLogsLevels.IllegalMap").format(levels))
    }
    // ValueSet object is a sorted set by design
    (SupportedLogDest.values.map(_ -> LogLevel.TRACE).toMap ++ levels).values.toArray |> setLogsLevels
  }

  def setLogPrefix(prefix: String): Unit = logPrefix = prefix

  def clearLogPrefix(): Unit = logPrefix = ""

}
