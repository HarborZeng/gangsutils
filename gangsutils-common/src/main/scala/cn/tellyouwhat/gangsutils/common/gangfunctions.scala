package cn.tellyouwhat.gangsutils.common

import cn.tellyouwhat.gangsutils.common.cc.Mappable
import cn.tellyouwhat.gangsutils.common.exceptions.GangException
import cn.tellyouwhat.gangsutils.common.logger.BaseLogger

import java.sql.{Connection, DriverManager}
import java.time.{Duration, Instant, LocalDate, LocalDateTime, ZoneId}
import java.util.Properties
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.SparkSession

import scala.language.implicitConversions
import scala.util._

/**
 * gang 函数库
 */
object gangfunctions {

  def reduceByKey[K, V](collection: Traversable[(K, V)])(implicit num: Numeric[V]): Map[K, V] = {
    import num._
    collection.groupBy(_._1).map {
      case (_: K@unchecked, traversable) => traversable.reduce { (a, b) => (a._1, a._2 + b._2) }
    }
  }

  /**
   * convert case class of Mappable to map of Any recursively
   */
  def ccToMap(cc: Mappable): Map[String, Any] = {
    if (cc == null)
      return null
    cc.getClass.getDeclaredFields.foldLeft(Map.empty[String, Any]) {
      (map, field) =>
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
    }
  }

  /**
   * convert object to map of Any recursively
   */
  def toMap(o: Any): Map[String, Any] = {
    if (o == null)
      return null
    o.getClass.getDeclaredFields.foldLeft(Map.empty[String, Any]) {
      (map, field) =>
        field.setAccessible(true)
        val value = field.get(o) match {
          case listOfM: List[Any] => listOfM.map(toMap)
          case None => null
          case Some(o) => o
          case _ => field.get(o)
        }
        map + (field.getName -> value)
    }
  }

  def endWithWPP(sl: String): Boolean =
    sl.endsWith("ppt") || sl.endsWith("pptx") || sl.endsWith("wpp")

  def endWithWPS(sl: String): Boolean =
    sl.endsWith("doc") || sl.endsWith("docx") || sl.endsWith("wps")

  def endWithET(sl: String): Boolean =
    sl.endsWith("xls") || sl.endsWith("xlsx") || sl.endsWith("et")

  def endWithPDF(sl: String): Boolean = sl.endsWith("pdf")

  def endWithImage(sl: String): Boolean =
    sl.endsWith("jpg") || sl.endsWith("jpeg") || sl.endsWith("png") || sl.endsWith("bmp") || sl.endsWith("gif")

  def endWithTxt(sl: String): Boolean = sl.endsWith("txt")

  implicit def chainSideEffect[A](a: A) = new {
    def withSideEffect(fun: A => Unit): A = {
      fun(a);
      a
    }

    def withSideEffectRT[T](fun: A => T): T = fun(a)

    def |!(fun: A => Unit): A = tap(fun)

    def tap(fun: A => Unit): A = withSideEffect(fun)

    /**
     * tap return with same type
     */
    def tapR(fun: A => A): A = withSideEffectRT[A](fun)

    /**
     * tap return with type T
     */
    def tapRT[T](fun: A => T): T = withSideEffectRT(fun)

    def |!![T](fun: A => T): T = tapRT[T](fun)
  }

  private def getFS(implicit spark: SparkSession): FileSystem =
    FileSystem.get(spark.sparkContext.hadoopConfiguration)

  def isPathExists(path: String)(implicit spark: SparkSession): Boolean =
    isPathExists(new Path(path))

  def isPathExists(path: Path)(implicit spark: SparkSession): Boolean =
    getFS.exists(path)

  /**
   * @return the modification time of file in milliseconds since January 1, 1970 UTC.
   */
  def fileModifiedTime(path: String)(implicit spark: SparkSession): Either[GangException, Long] =
    fileModifiedTime(new Path(path))

  /**
   * @return the modification time of file in milliseconds since January 1, 1970 UTC.
   */
  def fileModifiedTime(path: Path)(implicit spark: SparkSession): Either[GangException, Long] = {
    if (isPathExists(path)) {
      getFS.getFileStatus(path).getModificationTime |!! Right.apply
    } else {
      GangException(s"path：$path 不存在") |!! Left.apply
    }
  }

  def isSparkSaveDirExists(path: String)(implicit spark: SparkSession): Boolean =
    isPathExists(new Path(path, "_SUCCESS").toString)

  def isSparkSaveDirModifiedToday(path: String)(implicit spark: SparkSession): Boolean =
    fileModifiedTime(new Path(path, "_SUCCESS").toString) match {
      case Left(e) => throw GangException(s"获取 $path mtime 失败", e)
      case Right(mtime) => Instant.ofEpochMilli(mtime)
        .atZone(ZoneId.systemDefault()).toLocalDate
        .isEqual(LocalDate.now())
    }


  def isSparkSaveDirModifiedWithinNHours(path: String)(n: Int)(implicit spark: SparkSession): Boolean =
    fileModifiedTime(new Path(path, "_SUCCESS").toString) match {
      case Left(e) => throw GangException(s"获取 $path mtime 失败", e)
      case Right(mtime) => Instant.ofEpochMilli(mtime)
        .atZone(ZoneId.systemDefault()).toLocalDateTime
        .isAfter(LocalDateTime.now().minusHours(n))
    }

  def getMysql5Conn(connectionProperties: Properties)(host: String)(port: Int = 3306)(db: String)(encoding: String = "utf8"): Connection = {
    Class.forName("com.mysql.jdbc.Driver")
    DriverManager.getConnection(s"jdbc:mysql://$host:$port/$db?characterEncoding=$encoding", connectionProperties)
  }

  def timeit[R](block: => R)(desc: String)(implicit logger: BaseLogger): R = {
    logger.trace(s"开始$desc")
    val t0 = System.currentTimeMillis()
    val result = block // call-by-name
    val t1 = System.currentTimeMillis()
    logger.success(s"完成$desc，耗时${Duration.ofMillis(t1 - t0).toString.drop(2).toLowerCase}")
    result
  }

  import scala.io.AnsiColor.{RED, RESET}

  @annotation.tailrec
  def retry[T](n: Int)(fn: => T): Try[T] = {
    Try(fn) match {
      case Failure(e) if n > 1 =>
        println(s"【失败】${RED}执行失败，重试最后${n - 1}次$RESET", e.getMessage)
        retry(n - 1)(fn)
      case fn => fn
    }
  }

}
