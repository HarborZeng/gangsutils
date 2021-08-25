package cn.tellyouwhat.gangsutils.common.logger

import cn.tellyouwhat.gangsutils.common.helper.I18N

/**
 * 日志级别枚举
 */
object LogLevel extends Enumeration {
  /**
   * 跟踪级别日志枚举
   */
  val TRACE: LogLevel.Value = Value(I18N.getRB.getString("logLevel.trace"))
  /**
   * 信息级别日志枚举
   */
  val INFO: LogLevel.Value = Value(I18N.getRB.getString("logLevel.info"))
  /**
   * 成功级别日志枚举
   */
  val SUCCESS: LogLevel.Value = Value(I18N.getRB.getString("logLevel.success"))
  /**
   * 警告级别日志枚举
   */
  val WARNING: LogLevel.Value = Value(I18N.getRB.getString("logLevel.warning"))
  /**
   * 错误级别日志枚举
   */
  val ERROR: LogLevel.Value = Value(I18N.getRB.getString("logLevel.error"))
  /**
   * 致命级别日志枚举
   */
  val CRITICAL: LogLevel.Value = Value(I18N.getRB.getString("logLevel.critical"))
}