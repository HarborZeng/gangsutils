package cn.tellyouwhat.gangsutils.spark

import cn.tellyouwhat.gangsutils.core.exceptions.GangException
import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import org.apache.hadoop.fs.{FileSystem, Path, PathNotFoundException}
import org.apache.spark.sql.SparkSession

import java.time.{Instant, LocalDate, LocalDateTime, ZoneId}
import scala.util.{Either, Left, Right}

/**
 * gangsutils spark functions
 */
object funcs {

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
   * 获取 hdfs 目标路径的修改时间
   *
   * @param path  目标路径
   * @param spark sparkSession
   * @return the modification time of file in milliseconds since January 1, 1970 UTC.
   * @throws PathNotFoundException if the path does not exists
   */
  def fileModifiedTime(path: String)(implicit spark: SparkSession): Either[PathNotFoundException, Long] =
    fileModifiedTime(new Path(path))

  /**
   * 获取 hdfs 目标路径的修改时间
   *
   * @param path  目标路径
   * @param spark sparkSession
   * @return the modification time of file in milliseconds since January 1, 1970 UTC.
   * @throws PathNotFoundException if the path does not exists
   */
  def fileModifiedTime(path: Path)(implicit spark: SparkSession): Either[PathNotFoundException, Long] = {
    if (isPathExists(path)) {
      getFS.getFileStatus(path).getModificationTime |> Right.apply
    } else {
      new PathNotFoundException(path.toString) |> Left.apply
    }
  }

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
   * 获取文件系统
   *
   * @param spark sparkSession
   * @return 文件系统
   */
  private def getFS(implicit spark: SparkSession): FileSystem =
    FileSystem.get(spark.sparkContext.hadoopConfiguration)

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
      case Left(e) => throw GangException(s"error looking $path mtime", e)
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
      case Left(e) => throw GangException(s"error looking $path mtime", e)
      case Right(mtime) => Instant.ofEpochMilli(mtime)
        .atZone(ZoneId.systemDefault()).toLocalDateTime
        .isAfter(LocalDateTime.now().minusHours(n))
    }
}
