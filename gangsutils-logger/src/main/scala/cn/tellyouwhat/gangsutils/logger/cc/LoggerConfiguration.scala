package cn.tellyouwhat.gangsutils.logger.cc

import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger}

/**
 * LoggerConfiguration is for {@link Logger} sub-class configuration to control logger behavior
 *
 * @param isDTEnabled       is datetime enabled
 * @param isTraceEnabled    is trace enabled
 * @param isHostnameEnabled is hostname enabled
 * @param logPrefix         option of log prefix string
 * @param logLevel          one of {@link LogLevel} log level
 */
case class LoggerConfiguration(
                                isDTEnabled: Boolean = true,
                                isTraceEnabled: Boolean = false,
                                isHostnameEnabled: Boolean = true,
                                logPrefix: Option[String] = None,
                                logLevel: LogLevel.Value = LogLevel.TRACE,
                              )
