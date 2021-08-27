package cn.tellyouwhat.gangsutils.spark

import cn.tellyouwhat.gangsutils.core.exceptions.GangException
import org.apache.hadoop.fs.{FileSystem, Path, PathNotFoundException}
import org.apache.spark.sql.SparkSession
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}

import java.time.{LocalDateTime, ZoneId}

class funcsTest extends AnyFlatSpec with Matchers with PrivateMethodTester with BeforeAndAfter {

  implicit val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

  val workingDirectory: Path = {
    val getFS = PrivateMethod[FileSystem]('getFS)
    val fs = funcs invokePrivate getFS(spark)
    val wd = fs.getWorkingDirectory
    if (wd.toString.endsWith("-spark")) wd else wd.suffix("/gangsutils-spark")
  }

  val sparkJobDirPath = new Path(workingDirectory, "src/test/resources/spark_job_dir")
  val doesNotExistsDirPath = new Path(workingDirectory, "src/test/resources/does_not_exists")

  "getFS" should "get you a hadoop file system object" in {
    val getFS = PrivateMethod[FileSystem]('getFS)
    val fs = funcs invokePrivate getFS(spark)
    fs.getHomeDirectory.toString should startWith("file:/")
  }

  "isPathExists" should "test a string path existence" in {
    val sparkJobDirStringPath = sparkJobDirPath.toString
    val doesNotExistsDirStringPath = doesNotExistsDirPath.toString
    funcs.isPathExists(sparkJobDirStringPath) shouldBe true
    funcs.isPathExists(doesNotExistsDirStringPath) shouldBe false
  }

  it should "test a Path existence" in {
    funcs.isPathExists(sparkJobDirPath) shouldBe true
    funcs.isPathExists(doesNotExistsDirPath) shouldBe false
  }

  "fileModifiedTime" should "get mtime of string path" in {
    funcs.fileModifiedTime(sparkJobDirPath.toString) match {
      case Left(_) =>
      case Right(t) => t shouldBe >=(0L)
    }
    funcs.fileModifiedTime(doesNotExistsDirPath.toString) match {
      case Left(e) => a[PathNotFoundException] should be thrownBy (throw e)
      case Right(_) =>
    }
  }

  it should "get mtime of Path" in {
    funcs.fileModifiedTime(sparkJobDirPath) match {
      case Left(_) =>
      case Right(t) => t shouldBe >=(0L)
    }
    funcs.fileModifiedTime(doesNotExistsDirPath) match {
      case Left(e) => a[PathNotFoundException] should be thrownBy (throw e)
      case Right(_) =>
    }
  }

  "isSparkSaveDirExists" should "test a spark save dir string path existence" in {
    funcs.isSparkSaveDirExists(sparkJobDirPath.toString) shouldBe true
    funcs.isSparkSaveDirExists(doesNotExistsDirPath.toString) shouldBe false
  }

  "isSparkSaveDirModifiedToday" should "test a spark save dir whether modified today" in {
    val getFS = PrivateMethod[FileSystem]('getFS)
    val fs = funcs invokePrivate getFS(spark)
    val todayTimeMill = LocalDateTime.now()
      .atZone(ZoneId.systemDefault())
      .toInstant.toEpochMilli
    fs.setTimes(new Path(sparkJobDirPath, "_SUCCESS"), todayTimeMill, todayTimeMill)
    funcs.isSparkSaveDirModifiedToday(sparkJobDirPath.toString) shouldBe true

    val yesterdayTimeMill = LocalDateTime.now()
      .atZone(ZoneId.systemDefault())
      .minusDays(1).minusHours(1)
      .toInstant.toEpochMilli
    fs.setTimes(new Path(sparkJobDirPath, "_SUCCESS"), yesterdayTimeMill, yesterdayTimeMill)
    funcs.isSparkSaveDirModifiedToday(sparkJobDirPath.toString) shouldBe false

    a[GangException] should be thrownBy funcs.isSparkSaveDirModifiedToday(doesNotExistsDirPath.toString)
  }

  "isSparkSaveDirModifiedWithinNHours" should "test a spark save dir whether modified within n hours" in {
    val getFS = PrivateMethod[FileSystem]('getFS)
    val fs = funcs invokePrivate getFS(spark)
    val twoHoursAgoTimeMill = LocalDateTime.now()
      .atZone(ZoneId.systemDefault())
      .minusHours(2)
      .toInstant.toEpochMilli
    fs.setTimes(new Path(sparkJobDirPath, "_SUCCESS"), twoHoursAgoTimeMill, twoHoursAgoTimeMill)
    funcs.isSparkSaveDirModifiedWithinNHours(sparkJobDirPath.toString)(3) shouldBe true
    funcs.isSparkSaveDirModifiedWithinNHours(sparkJobDirPath.toString)(1) shouldBe false

    a[GangException] should be thrownBy funcs.isSparkSaveDirModifiedWithinNHours(doesNotExistsDirPath.toString)(1)
  }

}
