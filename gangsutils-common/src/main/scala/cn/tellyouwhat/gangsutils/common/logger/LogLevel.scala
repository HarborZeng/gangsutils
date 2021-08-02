package cn.tellyouwhat.gangsutils.common.logger

object LogLevel extends Enumeration {
  val TRACE: LogLevel.Value = Value("跟踪")
  val INFO: LogLevel.Value = Value("信息")
  val SUCCESS: LogLevel.Value = Value("成功")
  val WARNING: LogLevel.Value = Value("警告")
  val ERROR: LogLevel.Value = Value("错误")
  val CRITICAL: LogLevel.Value = Value("致命")
}