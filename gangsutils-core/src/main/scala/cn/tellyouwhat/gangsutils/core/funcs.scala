package cn.tellyouwhat.gangsutils.core

import cn.tellyouwhat.gangsutils.core.cc.Mappable
import cn.tellyouwhat.gangsutils.core.helper.I18N

import java.time.Duration
import scala.util.{Failure, Try}

/**
 * functions object for gangsutils
 */
object funcs {

  /**
   * group and for each group do a reduce by key
   *
   * @param collection the collection to reduce by key
   * @param num        import numeric operations
   * @tparam K key Type
   * @tparam V value Type
   * @return a map of key and reduce result
   */
  def reduceByKey[K, V](collection: Traversable[(K, V)])(implicit num: Numeric[V]): Map[K, V] = {
    import num._
    collection.groupBy(_._1).map {
      case (_: K@unchecked, traversable) => traversable.reduce { (a, b) => (a._1, a._2 + b._2) }
    }
  }

  /**
   * convert case class of {@link Mappable} to map of Any recursively
   *
   * @param cc the object to convert, note that the object must be a {@link Mappable} object
   */
  def ccToMap(cc: Mappable): Map[String, Any] = {
    if (cc == null)
      return null
    cc.getClass.getDeclaredFields.foldLeft(Map.empty[String, Any]) {
      // ignore $ or __ initialing members
      case (map, field) if !(field.getName.startsWith("$") || field.getName.startsWith("__")) =>
        field.setAccessible(true)
        val value = field.get(cc) match {
          case Some(m: Mappable) => ccToMap(m)
          // scala collection type is erased after compilation, so use 2 cases with if to identify List[Mappable]
          case Some(listOfM: List[Mappable@unchecked]) if listOfM.isEmpty => null
          case Some(listOfM: List[Mappable@unchecked]) if listOfM.head.isInstanceOf[Mappable] => listOfM.map(ccToMap)
          case m: Mappable => ccToMap(m)
          case None => null
          case Some(o) => o
          case _ => field.get(cc)
        }
        map + (field.getName -> value)
      case (map, _) => map
    }
  }

  /**
   * whether the input string ends with wpp type
   *
   * @param sl the input string
   * @return true if it ends, false otherwise
   */
  def endWithWPP(sl: String): Boolean =
    sl.endsWith(".ppt") || sl.endsWith(".pptx") || sl.endsWith(".wpp")

  /**
   * whether the input string ends with wps type
   *
   * @param sl the input string
   * @return true if it ends, false otherwise
   */
  def endWithWPS(sl: String): Boolean =
    sl.endsWith(".doc") || sl.endsWith(".docx") || sl.endsWith(".wps")

  /**
   * whether the input string ends with et type
   *
   * @param sl the input string
   * @return true if it ends, false otherwise
   */
  def endWithET(sl: String): Boolean =
    sl.endsWith(".xls") || sl.endsWith(".xlsx") || sl.endsWith(".et")

  /**
   * whether the input string ends with pdf type
   *
   * @param sl the input string
   * @return true if it ends, false otherwise
   */
  def endWithPDF(sl: String): Boolean = sl.endsWith(".pdf")

  /**
   * whether the input string ends with image type
   *
   * @param sl the input string
   * @return true if it ends, false otherwise
   */
  def endWithImage(sl: String): Boolean =
    sl.endsWith(".jpg") || sl.endsWith(".jpeg") || sl.endsWith(".png") || sl.endsWith(".bmp") || sl.endsWith(".gif")

  /**
   * whether the input string ends with text type
   *
   * @param sl the input string
   * @return true if it ends, false otherwise
   */
  def endWithTxt(sl: String): Boolean = sl.endsWith(".txt")

  /**
   * 重试一个函数
   *
   * @param n  尝试的总次数
   * @param fn 要执行的函数
   * @tparam T 尝试的函数的返回值类型
   * @return 尝试的函数的结果
   */
  @annotation.tailrec
  def retry[T](n: Int)(fn: => T): Try[T] = {
    Try(fn) match {
      case Failure(e) if n > 1 =>
        println(I18N.getRB.getString("retry.failure").format(n - 1, e))
        retry(n - 1)(fn)
      case fn => fn
    }
  }

  /**
   * strip ANSI color using regex replace
   *
   * @param s from where to strip
   * @return the striped string with ANSI color removed
   */
  def stripANSIColor(s: String): String = s.replaceAll("""\e\[[\d;]*[^\d;]""", "")

  def escapeQuotationMark(s: String): String = s.replace(""""""", """\"""")
  def escapeBackSlash(s: String): String = s.replace("""\""", """\\""")
  def escapeNewLine(s: String): String = s.replace("\n", "\\n")
  def escapeBackspace(s: String): String = s.replace("\b", "\\b")
  def escapeFormFeed(s: String): String = s.replace("\f", "\\f")
  def escapeCarriageReturn(s: String): String = s.replace("\r", "\\r")
  def escapeTab(s: String): String = s.replace("\t", "\\t")

  def escapeJsonString(s: String): String =
    // the order matters
    s |> escapeBackSlash |> escapeQuotationMark |> escapeNewLine |> escapeBackspace |> escapeFormFeed |> escapeCarriageReturn |> escapeTab


  /**
   * 使用字符串描述的两个时间差，如 1m20.3s
   *
   * @param t0 先前的时间
   * @param t1 后来的时间
   * @return 描述时间的字符串
   */
  def calcExecDuration(t0: Long, t1: Long): String =
    Duration.ofMillis(t1 - t0).toString.drop(2).toLowerCase

}
