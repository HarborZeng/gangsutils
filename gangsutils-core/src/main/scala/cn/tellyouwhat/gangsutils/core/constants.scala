package cn.tellyouwhat.gangsutils.core

import cn.tellyouwhat.gangsutils.core.helper.I18N.getRB

import java.util.regex.Pattern
import scala.io.AnsiColor._

/**
 * constants object for gangsutils
 */
object constants {

  /**
   * unquote square brackets for println
   */
  val leftSB_unquote: String = getRB.getString("left_square_bracket")
  val rightSB_unquote: String = getRB.getString("right_square_bracket")
  /**
   * quoted square brackets for regex match
   */
  val leftSB: String = Pattern.quote(leftSB_unquote)
  val rightSB: String = Pattern.quote(rightSB_unquote)

  /**
   * heads with quote square bracket for regex match
   */
  val traceHead: String = s"$leftSB${getRB.getString("logLevel.trace")}$rightSB"
  val criticalHead: String = s"$leftSB${getRB.getString("logLevel.critical")}$rightSB"
  val errorHead: String = s"$leftSB${getRB.getString("logLevel.error")}$rightSB"
  val infoHead: String = s"$leftSB${getRB.getString("logLevel.info")}$rightSB"
  val successHead: String = s"$leftSB${getRB.getString("logLevel.success")}$rightSB"
  val warningHead: String = s"$leftSB${getRB.getString("logLevel.warning")}$rightSB"
  val placeholderHead: String = s"$leftSB%s$rightSB"
  /**
   * heads with unquote square bracket for println
   */
  val traceHead_unquote: String = s"$leftSB_unquote${getRB.getString("logLevel.trace")}$rightSB_unquote"
  val criticalHead_unquote: String = s"$leftSB_unquote${getRB.getString("logLevel.critical")}$rightSB_unquote"
  val errorHead_unquote: String = s"$leftSB_unquote${getRB.getString("logLevel.error")}$rightSB_unquote"
  val infoHead_unquote: String = s"$leftSB_unquote${getRB.getString("logLevel.info")}$rightSB_unquote"
  val successHead_unquote: String = s"$leftSB_unquote${getRB.getString("logLevel.success")}$rightSB_unquote"
  val warningHead_unquote: String = s"$leftSB_unquote${getRB.getString("logLevel.warning")}$rightSB_unquote"
  val placeholderHead_unquote: String = s"$leftSB_unquote%s$rightSB_unquote"
  /**
   * regex quoted color pattern for regex match
   */
  val yellowPattern: String = Pattern.quote(YELLOW)
  val redPattern: String = Pattern.quote(RED)
  val boldPattern: String = Pattern.quote(BOLD)
  val greenPattern: String = Pattern.quote(GREEN)
  val resetPattern: String = Pattern.quote(RESET)
  /**
   * placeholders for regex match
   */
  val infoLog: String = s"$boldPattern$infoHead$resetPattern%s\\s+"
  val successLog: String = s"$greenPattern$successHead$resetPattern%s\\s+"
  val warningLog: String = s"$yellowPattern$warningHead$resetPattern%s\\s+"
  val criticalLog: String = s"$redPattern$boldPattern$criticalHead$resetPattern%s\\s+"
  val errorLog: String = s"$redPattern$errorHead$resetPattern%s\\s+"
  val traceLog: String = s"$traceHead$resetPattern%s\\s+"
  /**
   * placeholders with unquote head for println
   */
  val infoLog_unquote: String = s"$BOLD$infoHead_unquote$RESET%s"
  val successLog_unquote: String = s"$GREEN$successHead_unquote$RESET%s"
  val warningLog_unquote: String = s"$YELLOW$warningHead_unquote$RESET%s"
  val criticalLog_unquote: String = s"$RED$BOLD$criticalHead_unquote$RESET%s"
  val errorLog_unquote: String = s"$RED$errorHead_unquote$RESET%s"
  val traceLog_unquote: String = s"$traceHead_unquote$RESET%s"
  val datetimeRe = """\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d+"""

}
