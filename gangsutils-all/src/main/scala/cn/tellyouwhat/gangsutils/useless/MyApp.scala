package cn.tellyouwhat.gangsutils.useless

import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.{LOCAL_HTML_LOGGER, LOCAL_PLAIN_TEXT_LOGGER, PRINTLN_LOGGER, WOA_WEBHOOK_LOGGER}
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.dest.fs.{LocalHtmlLogger, LocalPlainTextLogger}
import cn.tellyouwhat.gangsutils.logger.dest.webhook.WoaWebhookLogger
import cn.tellyouwhat.gangsutils.logger.funcs.timeit
import cn.tellyouwhat.gangsutils.logger.helper.{Timeit, TimeitLogger}


/**
 * 代码实验田
 */
class MyApp extends Timeit {

  private val logger: GangLogger = GangLogger.getLogger

  override def run(desc: String): Unit = {
    timeit(
      logger.info("123")
    )
  }

}

object MyApp {

  private implicit var logger: GangLogger = _

  def main(args: Array[String]): Unit = {
    import GangLogger.blockToThunk
    GangLogger.setLoggerAndConfigurationAndInitBlock(Seq(
      PRINTLN_LOGGER -> (LoggerConfiguration(async = false, isTraceEnabled = true), {}),
      WOA_WEBHOOK_LOGGER -> (LoggerConfiguration(async = false), WoaWebhookLogger.initializeWoaWebhook("a35a9ed09b9a7bb50dc5cc13c4cc20af")),
      LOCAL_HTML_LOGGER -> (LoggerConfiguration(async = false), LocalHtmlLogger.setLogSavePath("html_log/3.html")),
      LOCAL_PLAIN_TEXT_LOGGER -> (LoggerConfiguration(async = false), LocalPlainTextLogger.setLogSavePath("text_log/3.txt")),
    ))
    logger = GangLogger()
    logger.trace("tracing")

    try {
      1 / 0
    } catch {
      case e: Exception => logger.error("1/0", e)
    }

    MyApp().run()

  }

  def apply(): MyApp = new MyApp() with TimeitLogger
}
