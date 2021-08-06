package cn.tellyouwhat.gangsutils.common.logger

/**
 * 日志级别枚举
 */
object LogLevel extends Enumeration {
  /**
   * 跟踪级别日志枚举
   */
  val TRACE: LogLevel.Value = Value("跟踪")
  /**
   * 信息级别日志枚举
   */
  val INFO: LogLevel.Value = Value("信息")
  /**
   * 成功级别日志枚举
   */
  val SUCCESS: LogLevel.Value = Value("成功")
  /**
   * 警告级别日志枚举
   */
  val WARNING: LogLevel.Value = Value("警告")
  /**
   * 错误级别日志枚举
   */
  val ERROR: LogLevel.Value = Value("错误")
  /**
   * 致命级别日志枚举
   */
  val CRITICAL: LogLevel.Value = Value("致命")
}