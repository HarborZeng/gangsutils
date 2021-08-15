package cn.tellyouwhat.gangsutils.common

import cn.tellyouwhat.gangsutils.common.cc.Mappable
import cn.tellyouwhat.gangsutils.common.exceptions.GangException
import cn.tellyouwhat.gangsutils.common.helper.chaining.PipeIt
import cn.tellyouwhat.gangsutils.common.logger.{BaseLogger, LogLevel}

import java.time.{Duration, Instant, LocalDate, LocalDateTime, ZoneId}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.SparkSession

import scala.language.implicitConversions
import scala.util._

/**
 * gang 函数库
 */
object gangfunctions {

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
      // ignore $ initial member
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
    sl.endsWith("ppt") || sl.endsWith("pptx") || sl.endsWith("wpp")

  /**
   * whether the input string ends with wps type
   *
   * @param sl the input string
   * @return true if it ends, false otherwise
   */
  def endWithWPS(sl: String): Boolean =
    sl.endsWith("doc") || sl.endsWith("docx") || sl.endsWith("wps")

  /**
   * whether the input string ends with et type
   *
   * @param sl the input string
   * @return true if it ends, false otherwise
   */
  def endWithET(sl: String): Boolean =
    sl.endsWith("xls") || sl.endsWith("xlsx") || sl.endsWith("et")

  /**
   * whether the input string ends with pdf type
   *
   * @param sl the input string
   * @return true if it ends, false otherwise
   */
  def endWithPDF(sl: String): Boolean = sl.endsWith("pdf")

  /**
   * whether the input string ends with image type
   *
   * @param sl the input string
   * @return true if it ends, false otherwise
   */
  def endWithImage(sl: String): Boolean =
    sl.endsWith("jpg") || sl.endsWith("jpeg") || sl.endsWith("png") || sl.endsWith("bmp") || sl.endsWith("gif")

  /**
   * whether the input string ends with text type
   *
   * @param sl the input string
   * @return true if it ends, false otherwise
   */
  def endWithTxt(sl: String): Boolean = sl.endsWith("txt")

  /**
   * 获取文件系统
   *
   * @param spark sparkSession
   * @return 文件系统
   */
  private def getFS(implicit spark: SparkSession): FileSystem =
    FileSystem.get(spark.sparkContext.hadoopConfiguration)

  /**
   * 查看 hdfs 目标路径是否存在
   *
   * @param path  目标路径
   * @param spark sparkSession
   * @return true if the path exists, false otherwise
   */
  def isPathExists(path: String)(implicit spark: SparkSession): Boolean =
    isPathExists(new Path(path))

  /**
   * 查看 hdfs 目标路径是否存在
   *
   * @param path  目标路径
   * @param spark sparkSession
   * @return true if the path exists, false otherwise
   */
  def isPathExists(path: Path)(implicit spark: SparkSession): Boolean =
    getFS.exists(path)

  /**
   * 获取 hdfs 目标路径的修改时间
   *
   * @param path  目标路径
   * @param spark sparkSession
   * @return the modification time of file in milliseconds since January 1, 1970 UTC.
   * @throws GangException if the path does not exists
   */
  def fileModifiedTime(path: String)(implicit spark: SparkSession): Either[GangException, Long] =
    fileModifiedTime(new Path(path))

  /**
   * 获取 hdfs 目标路径的修改时间
   *
   * @param path  目标路径
   * @param spark sparkSession
   * @return the modification time of file in milliseconds since January 1, 1970 UTC.
   * @throws GangException if the path does not exists
   */
  def fileModifiedTime(path: Path)(implicit spark: SparkSession): Either[GangException, Long] = {
    if (isPathExists(path)) {
      getFS.getFileStatus(path).getModificationTime |> Right.apply
    } else {
      GangException(s"path：$path 不存在") |> Left.apply
    }
  }

  /**
   * spark 保存的路径是否已存在
   *
   * @param path  spark 保存的路径
   * @param spark sparkSession
   * @return true if the path exists, false otherwise
   */
  def isSparkSaveDirExists(path: String)(implicit spark: SparkSession): Boolean =
    isPathExists(new Path(path, "_SUCCESS"))

  /**
   * spark 保存的路径是否已存在
   *
   * @param path  spark 保存的路径
   * @param spark sparkSession
   * @return true if the path exists, false otherwise
   * @throws GangException if failed to get the mtime of the path
   */
  def isSparkSaveDirModifiedToday(path: String)(implicit spark: SparkSession): Boolean =
    fileModifiedTime(new Path(path, "_SUCCESS")) match {
      case Left(e) => throw GangException(s"获取 $path mtime 失败", e)
      case Right(mtime) => Instant.ofEpochMilli(mtime)
        .atZone(ZoneId.systemDefault()).toLocalDate
        .isEqual(LocalDate.now())
    }


  /**
   * spark 保存的路径是否已存在 n 小时
   *
   * @param path  spark 保存的路径
   * @param n     n 小时
   * @param spark sparkSession
   * @return true if the path exists, false otherwise
   * @throws GangException if failed to get the mtime of the path
   */
  def isSparkSaveDirModifiedWithinNHours(path: String)(n: Int)(implicit spark: SparkSession): Boolean =
    fileModifiedTime(new Path(path, "_SUCCESS")) match {
      case Left(e) => throw GangException(s"获取 $path mtime 失败", e)
      case Right(mtime) => Instant.ofEpochMilli(mtime)
        .atZone(ZoneId.systemDefault()).toLocalDateTime
        .isAfter(LocalDateTime.now().minusHours(n))
    }

  /**
   * print 或者执行 logger
   *
   * @param content 要输出的内容
   * @param level   日志级别
   * @param logger  日志对象
   */
  def printOrLog(content: String, level: LogLevel.Value = LogLevel.TRACE)(implicit logger: BaseLogger = null): Unit =
    if (logger == null) {
      println(s"【$level】: $content")
    } else {
      logger.log(content, level)
    }

  /**
   * 计时 + 切面日志
   *
   * @param block  要执行的方法
   * @param desc   描述，将作用于切面日志
   * @param logger 日志对象
   * @tparam R 返回值 Type
   * @return block 的执行结果
   */
  // TODO remove implicit logger
  def timeit[R](block: => R, desc: String = "任务")(implicit logger: BaseLogger = null): R = {
    printOrLog(s"开始$desc")
    val t0 = System.currentTimeMillis()
    val result = Try(block) match {
      case Failure(e) =>
        val t1 = System.currentTimeMillis()
        printOrLog(s"执行${desc}失败，耗时${calcExecDuration(t0, t1)}", level = LogLevel.CRITICAL)
        throw e
      case Success(v) => v
    }
    val t1 = System.currentTimeMillis()
    printOrLog(s"完成$desc，耗时${calcExecDuration(t0, t1)}", level = LogLevel.SUCCESS)
    result
  }

  /**
   * 使用字符串描述的两个时间差，如 1m20.3s
   *
   * @param t0 先前的时间
   * @param t1 后来的时间
   * @return 描述时间的字符串
   */
  private def calcExecDuration(t0: Long, t1: Long): String =
    Duration.ofMillis(t1 - t0).toString.drop(2).toLowerCase

  /**
   * 重试一个函数
   *
   * @param n      尝试的总次数
   * @param fn     要执行的函数
   * @param logger 如果失败打印日志时所使用的日志对象
   * @tparam T 尝试的函数的返回值类型
   * @return 尝试的函数的结果
   */
  @annotation.tailrec
  def retry[T](n: Int)(fn: => T)(implicit logger: BaseLogger = null): Try[T] = {
    Try(fn) match {
      case Failure(e) if n > 1 =>
        printOrLog(s"执行失败，重试最后${n - 1}次，error: $e", level = LogLevel.ERROR)
        retry(n - 1)(fn)
      case fn => fn
    }
  }

}
