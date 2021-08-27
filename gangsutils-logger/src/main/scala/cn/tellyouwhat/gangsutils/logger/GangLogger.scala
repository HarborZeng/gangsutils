package cn.tellyouwhat.gangsutils.logger

import cn.tellyouwhat.gangsutils.core.helper.I18N
import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest._
import cn.tellyouwhat.gangsutils.logger.dest.PrintlnLogger
import cn.tellyouwhat.gangsutils.logger.dest.webhook._
import cn.tellyouwhat.gangsutils.logger.exceptions.NoAliveLoggerException


/**
 * Logger 的具体实现，混入了 PrintlnLogger 和 WoaWebhookLogger
 */
protected class GangLogger(
                            override val isDTEnabled: Boolean = GangLogger.isDTEnabled,
                            override val isTraceEnabled: Boolean = GangLogger.isTraceEnabled,
                            override implicit val defaultLogDest: Seq[SupportedLogDest.Value] = GangLogger.defaultLogDest,
                            override val logsLevels: Array[LogLevel.Value] = GangLogger.logsLevels,
                            override val logPrefix: Option[String] = GangLogger.logPrefix,
                            override val isHostnameEnabled: Boolean = GangLogger.isHostnameEnabled,
                          ) extends PrintlnLogger with WoaWebhookLogger with SlackWebhookLogger with QYWXWebhookLogger with DingTalkWebhookLogger with ServerChanWebhookLogger with FeishuWebhookLogger with TelegramWebhookLogger {

  override def log(msg: Any, level: LogLevel.Value)(implicit enabled: Seq[SupportedLogDest.Value] = defaultLogDest): Boolean = {
    if (msg == null) false
    else {
      val msgStr = msg.toString
      val logStatus = Array.newBuilder[Boolean]
      if (enabled.contains(PRINTLN_LOGGER) && level >= logsLevels(PRINTLN_LOGGER.id)) {
        val status = super[PrintlnLogger].doTheLogAction(msgStr, level)
        logStatus += status
      }
      if (enabled.contains(WOA_WEBHOOK_LOGGER) && level >= logsLevels(WOA_WEBHOOK_LOGGER.id)) {
        val status = doTheLogAction4WOA(msgStr, level)
        logStatus += status
      }
      if (enabled.contains(SLACK_WEBHOOK_LOGGER) && level >= logsLevels(SLACK_WEBHOOK_LOGGER.id)) {
        val status = doTheLogAction4Slack(msgStr, level)
        logStatus += status
      }
      if (enabled.contains(QYWX_WEBHOOK_LOGGER) && level >= logsLevels(QYWX_WEBHOOK_LOGGER.id)) {
        val status = doTheLogAction4QYWX(msgStr, level)
        logStatus += status
      }
      if (enabled.contains(DINGTALK_WEBHOOK_LOGGER) && level >= logsLevels(DINGTALK_WEBHOOK_LOGGER.id)) {
        val status = doTheLogAction4DingTalk(msgStr, level)
        logStatus += status
      }
      if (enabled.contains(SERVERCHAN_WEBHOOK_LOGGER) && level >= logsLevels(SERVERCHAN_WEBHOOK_LOGGER.id)) {
        val status = doTheLogAction4ServerChan(msgStr, level)
        logStatus += status
      }
      if (enabled.contains(FEISHU_WEBHOOK_LOGGER) && level >= logsLevels(FEISHU_WEBHOOK_LOGGER.id)) {
        val status = doTheLogAction4Feishu(msgStr, level)
        logStatus += status
      }
      if (enabled.contains(TELEGRAM_WEBHOOK_LOGGER) && level >= logsLevels(TELEGRAM_WEBHOOK_LOGGER.id)) {
        val status = doTheLogAction4Telegram(msgStr, level)
        logStatus += status
      }
      logStatus.result().forall(b => b)
    }
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

  private def doTheLogAction4DingTalk(msg: String, level: LogLevel.Value): Boolean = {
    super[DingTalkWebhookLogger].checkPrerequisite()
    super[DingTalkWebhookLogger].webhookLog(msg, level)
  }

  private def doTheLogAction4ServerChan(msg: String, level: LogLevel.Value): Boolean = {
    super[ServerChanWebhookLogger].checkPrerequisite()
    super[ServerChanWebhookLogger].webhookLog(msg, level)
  }

  private def doTheLogAction4Feishu(msg: String, level: LogLevel.Value): Boolean = {
    super[FeishuWebhookLogger].checkPrerequisite()
    super[FeishuWebhookLogger].webhookLog(msg, level)
  }

  private def doTheLogAction4Telegram(msg: String, level: LogLevel.Value): Boolean = {
    super[TelegramWebhookLogger].checkPrerequisite()
    super[TelegramWebhookLogger].webhookLog(msg, level)
  }
}

object GangLogger {

  /**
   * Logger 的单例对象
   */
  private[logger] var _logger: Option[Logger] = None
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
  private var logPrefix: Option[String] = None

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
             logPrefix: Option[String] = logPrefix,
             isHostnameEnabled: Boolean = isHostnameEnabled
           ): GangLogger =
    new GangLogger(isDTEnabled, isTraceEnabled, defaultLogDest, logsLevels, logPrefix, isHostnameEnabled) |! (l => _logger = Some(l))

  /**
   * 获取 Logger 单例对象
   *
   * @return
   */
  def getLogger: Logger = {
    _logger match {
      case Some(logger) => logger
      case None => apply() |! (logger => logger.warning(NoAliveLoggerException(I18N.getRB.getString("getLogger.NoAliveLogger"))))
    }
  }

  /**
   * 创建一个新的 GangLogger 实例
   *
   * @return 一个新的 GangLogger 实例
   */
  def apply(): GangLogger = new GangLogger() |! (l => _logger = Some(l))

  /**
   * 清除单例 Logger 对象
   */
  def killLogger(): Unit = _logger = None

  /**
   * 将 GangLogger 伴生对像变量初始化
   */
  def resetLoggerConfig(): Unit = {
    isDTEnabled = true
    isTraceEnabled = false
    defaultLogDest = Seq(PRINTLN_LOGGER)
    logsLevels = Array.fill(SupportedLogDest.values.size)(LogLevel.TRACE)
    logPrefix = None
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

  def setLogPrefix(prefix: String): Unit = logPrefix = Some(prefix)

  def clearLogPrefix(): Unit = logPrefix = None

}