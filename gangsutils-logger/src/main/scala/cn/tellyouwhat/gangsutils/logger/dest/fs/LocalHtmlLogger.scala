package cn.tellyouwhat.gangsutils.logger.dest.fs
import cn.tellyouwhat.gangsutils.core.funcs.stripANSIColor
import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.logger.LogLevel

trait LocalHtmlLogger extends LocalFileLogger{

}

object LocalHtmlLogger {
  val LOCAL_HTML_LOGGER = "local_html_logger"
}