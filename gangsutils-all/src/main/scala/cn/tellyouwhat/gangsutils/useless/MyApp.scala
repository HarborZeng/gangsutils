package cn.tellyouwhat.gangsutils.useless

import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.{LOCAL_HTML_LOGGER, LOCAL_PLAIN_TEXT_LOGGER, PRINTLN_LOGGER, WOA_WEBHOOK_LOGGER}
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.dest.fs.{LocalHtmlLogger, LocalPlainTextLogger}
import cn.tellyouwhat.gangsutils.logger.dest.webhook.WoaWebhookLogger
import cn.tellyouwhat.gangsutils.logger.helper.{Timeit, TimeitLogger}


class MyApp extends Timeit {

  private implicit val logger: GangLogger = GangLogger.getLogger

  override def run(desc: String): Unit = {
    logger.info(desc)
  }
}

object MyApp {

  def main(args: Array[String]): Unit = {
    WoaWebhookLogger.initializeWoaWebhook("a35a9ed09b9a7bb50dc5cc13c4cc20af")
    LocalHtmlLogger.setLogSavePath("html_log/3.html")
    LocalPlainTextLogger.setLogSavePath("text_log/3.txt")
    GangLogger.setLoggerAndConfiguration(Seq(
      PRINTLN_LOGGER -> LoggerConfiguration(isTraceEnabled = true),
      WOA_WEBHOOK_LOGGER -> LoggerConfiguration(),
      LOCAL_HTML_LOGGER -> LoggerConfiguration(),
      LOCAL_PLAIN_TEXT_LOGGER -> LoggerConfiguration(),
    ))
    val logger = GangLogger()
    logger.trace("tracing")(enabled = Seq(PRINTLN_LOGGER))

  }

  def apply(): MyApp = new MyApp() with TimeitLogger
}
