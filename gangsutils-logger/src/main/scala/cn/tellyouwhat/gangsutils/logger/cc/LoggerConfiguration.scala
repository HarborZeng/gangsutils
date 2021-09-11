package cn.tellyouwhat.gangsutils.logger.cc

import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger}

/**
 * LoggerConfiguration is for [[Logger]] sub-class configuration to control logger behavior
 *
 * @param isDTEnabled       is datetime enabled
 * @param isTraceEnabled    is trace enabled
 * @param isHostnameEnabled is hostname enabled
 * @param logPrefix         option of log prefix string
 * @param logLevel          one of [[LogLevel]] log level
 * @param async             do the log action asynchronously or not
 */
case class LoggerConfiguration(
                                isDTEnabled: Boolean = true,
                                isTraceEnabled: Boolean = false,
                                isHostnameEnabled: Boolean = true,
                                logPrefix: Option[String] = None,
                                logLevel: LogLevel.Value = LogLevel.TRACE,
                                async: Boolean = false,
                              )
