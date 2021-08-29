package cn.tellyouwhat.gangsutils.logger.cc

import cn.tellyouwhat.gangsutils.logger.LogLevel

case class LoggerConfiguration(
                                isDTEnabled: Boolean = true,
                                isTraceEnabled: Boolean = false,
                                isHostnameEnabled: Boolean = true,
                                logPrefix: Option[String] = None,
                                logLevel: LogLevel.Value = LogLevel.TRACE,
                              )
