package cn.tellyouwhat.gangsutils.common

import cn.tellyouwhat.gangsutils.common.cc.Mappable
import cn.tellyouwhat.gangsutils.common.exceptions.GangException
import cn.tellyouwhat.gangsutils.common.gangconstants.{criticalHead, criticalLog, errorLog, successHead, successLog, traceHead, traceLog}
import cn.tellyouwhat.gangsutils.common.helper.I18N.getRB
import cn.tellyouwhat.gangsutils.common.logger.{GangLogger, LogLevel}
import org.apache.hadoop.fs.{FileSystem, Path, PathNotFoundException}
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.ByteArrayOutputStream
import java.time.{LocalDateTime, ZoneId}
import scala.util.{Failure, Success}

class gangfunctionsTest extends AnyFlatSpec with Matchers with PrivateMethodTester with BeforeAndAfter {

  before {
    GangLogger.disableDateTime()
    GangLogger.disableHostname()
    GangLogger()
  }

  after {
    GangLogger.killLogger()
    GangLogger.resetLoggerConfig()
  }

  behavior of "gangfunctionsTest"

  "endWithET" should "test a file whether ends with et extensions" in {
    gangfunctions.endWithET("abc.et") shouldBe true
    gangfunctions.endWithET("abc.xls") shouldBe true
    gangfunctions.endWithET("abc.xlsx") shouldBe true
    gangfunctions.endWithET("abc") shouldBe false
    gangfunctions.endWithET("") shouldBe false
    a[NullPointerException] should be thrownBy gangfunctions.endWithET(null)
  }

  "endWithTxt" should "test a file whether ends with txt extension" in {
    gangfunctions.endWithTxt("abc.txt") shouldBe true
    gangfunctions.endWithTxt("abc") shouldBe false
    gangfunctions.endWithTxt("") shouldBe false
    a[NullPointerException] should be thrownBy gangfunctions.endWithTxt(null)
  }

  "endWithWPS" should "test a file whether ends with wps extensions" in {
    gangfunctions.endWithWPS("abc.doc") shouldBe true
    gangfunctions.endWithWPS("abc.docx") shouldBe true
    gangfunctions.endWithWPS("abc.wps") shouldBe true
    gangfunctions.endWithWPS("abc") shouldBe false
    gangfunctions.endWithWPS("") shouldBe false
    a[NullPointerException] should be thrownBy gangfunctions.endWithWPS(null)
  }

  "endWithWPP" should "test a file whether ends with wpp extensions" in {
    gangfunctions.endWithWPP("abc.ppt") shouldBe true
    gangfunctions.endWithWPP("abc.pptx") shouldBe true
    gangfunctions.endWithWPP("abc.wpp") shouldBe true
    gangfunctions.endWithWPP("abc") shouldBe false
    gangfunctions.endWithWPP("") shouldBe false
    a[NullPointerException] should be thrownBy gangfunctions.endWithWPP(null)
  }

  "endWithImage" should "test a file whether ends with image extensions" in {
    gangfunctions.endWithImage("abc.jpg") shouldBe true
    gangfunctions.endWithImage("abc.jpeg") shouldBe true
    gangfunctions.endWithImage("abc.png") shouldBe true
    gangfunctions.endWithImage("abc.bmp") shouldBe true
    gangfunctions.endWithImage("abc.gif") shouldBe true
    gangfunctions.endWithImage("abc") shouldBe false
    gangfunctions.endWithImage("") shouldBe false
    a[NullPointerException] should be thrownBy gangfunctions.endWithImage(null)
  }

  "endWithPDF" should "test a file whether ends with pdf extensions" in {
    gangfunctions.endWithPDF("abc.pdf") shouldBe true
    gangfunctions.endWithPDF("abc") shouldBe false
    gangfunctions.endWithPDF("") shouldBe false
    a[NullPointerException] should be thrownBy gangfunctions.endWithPDF(null)
  }

  "ccToMap" should "convert a Mappable to Map" in {
    case class EntityIn(
                         str: String,
                         bool: Boolean,
                         int: Int,
                         long: Long,
                         short: Short,
                         bigInt: BigInt,
                         bigDecimal: BigDecimal,
                         double: Double,
                         float: Float,
                         sss: Seq[String],
                         ssl: List[String],
                         ssa: Array[String],
                         sm: Seq[Map[String, Int]],
                         v: Option[BigInt],
                         map: Map[String, Double],
                       ) extends Mappable
    case class EntityOut(
                          str: String,
                          bool: Boolean,
                          int: Int,
                          long: Long,
                          short: Short,
                          bigInt: BigInt,
                          bigDecimal: BigDecimal,
                          double: Double,
                          float: Float,
                          sss: Seq[String],
                          ssl: List[String],
                          ssa: Array[String],
                          sm: Seq[Map[String, Int]],
                          v: Option[BigInt],
                          map: Map[String, Double],
                          cc: EntityIn,
                        ) extends Mappable
    val in: EntityIn = EntityIn(
      str = "some string",
      bool = true,
      int = 2,
      long = 100L,
      short = 5,
      bigInt = 100000,
      bigDecimal = 10000,
      double = 0.5,
      float = 0.5F,
      sss = Seq("abc", "def"),
      ssl = List("a", "b"),
      ssa = Array("c", "d"),
      sm = Seq(Map("a" -> 2, "b" -> 3)),
      v = Some(10),
      map = Map("c" -> 0.4d, "d" -> 0.5d)
    )
    val out: EntityOut = EntityOut(
      str = "some string",
      bool = true,
      int = 2,
      long = 100L,
      short = 5,
      bigInt = 100000,
      bigDecimal = 10000,
      double = 0.5,
      float = 0.5F,
      sss = Seq("abc", "def"),
      ssl = List("a", "b"),
      ssa = Array("c", "d"),
      sm = Seq(Map("a" -> 2, "b" -> 3)),
      v = Some(10),
      map = Map("c" -> 0.4d, "d" -> 0.5d),
      cc = in
    )
    val map = gangfunctions.ccToMap(out)
    map should have size 16
    map("str") shouldBe "some string"
    map("bool") shouldBe true
    map("int") shouldBe 2
    map("long") shouldBe 100L
    map("short") shouldBe 5
    map("bigInt") shouldBe 100000
    map("bigDecimal") shouldBe 10000
    map("double") shouldBe 0.5
    map("float") shouldBe 0.5F
    map("sss").asInstanceOf[Seq[String]] should contain theSameElementsAs Seq("abc", "def")
    map("ssl").asInstanceOf[List[String]] should contain theSameElementsAs Seq("a", "b")
    map("ssa").asInstanceOf[Array[String]] should contain theSameElementsAs Seq("c", "d")
    val stringToInts = map("sm").asInstanceOf[Seq[Map[String, Int]]]
    stringToInts should have length 1
    val stringToInt = stringToInts.head
    stringToInt should have size 2
    stringToInt should contain allOf("a" -> 2, "b" -> 3)
    map("v") shouldBe 10
    val stringToDouble = map("map").asInstanceOf[Map[String, Double]]
    stringToDouble should have size 2
    stringToDouble should contain allOf("c" -> 0.4d, "d" -> 0.5d)
    val inMap = map("cc").asInstanceOf[Map[String, Any]]
    inMap should have size 15
    inMap("str") shouldBe "some string"
    inMap("bool") shouldBe true
    inMap("int") shouldBe 2
    inMap("long") shouldBe 100L
    inMap("short") shouldBe 5
    inMap("bigInt") shouldBe 100000
    inMap("bigDecimal") shouldBe 10000
    inMap("double") shouldBe 0.5
    inMap("float") shouldBe 0.5F
    inMap("sss").asInstanceOf[Seq[String]] should contain theSameElementsAs Seq("abc", "def")
    inMap("ssl").asInstanceOf[List[String]] should contain theSameElementsAs Seq("a", "b")
    inMap("ssa").asInstanceOf[Array[String]] should contain theSameElementsAs Seq("c", "d")
    val stringToIntsIn = inMap("sm").asInstanceOf[Seq[Map[String, Int]]]
    stringToIntsIn should have length 1
    val stringToIntIn = stringToIntsIn.head
    stringToIntIn should have size 2
    stringToIntIn should contain allOf("a" -> 2, "b" -> 3)
    inMap("v") shouldBe 10
    val stringToDoubleIn = inMap("map").asInstanceOf[Map[String, Double]]
    stringToDoubleIn should have size 2
    stringToDoubleIn should contain allOf("c" -> 0.4d, "d" -> 0.5d)
  }

  it should "get null if the parameter is null" in {
    gangfunctions.ccToMap(null) shouldBe null
  }

  "timeit" should "time a function invocation and log the start and execution duration" in {
    val stream = new ByteArrayOutputStream()
    Console.withOut(stream) {
      gangfunctions.timeit(1 + 1) shouldBe 2
    }

    stream.toString() should fullyMatch regex
      traceLog.format(s": ${getRB.getString("timeit.start").format(getRB.getString("task"))}") +
        successLog.format(s": ${getRB.getString("timeit.finished").format(getRB.getString("task"), """\d*\.*\d*s""")}")

    stream.reset()
    Console.withOut(stream) {
      a[ArithmeticException] should be thrownBy {
        gangfunctions.timeit(1 / 0)
      }
    }
    stream.toString() should fullyMatch regex
      traceLog.format(s": ${getRB.getString("timeit.start").format(getRB.getString("task"))}") +
        criticalLog.format(s": ${getRB.getString("timeit.failed").format(getRB.getString("task"), """\d*\.*\d*s""")}")
  }

  "reduceByKey" should "group and for each group do a reduce by key" in {
    val ext_1 = Array("txt", "txt", "pdf", "docx", "docx", "ppt", "ppt", "ppt").map((_, 1))
    val ext_count = gangfunctions.reduceByKey(ext_1)
    ext_count should have size 4
    ext_count should contain allOf("txt" -> 2, "pdf" -> 1, "docx" -> 2, "ppt" -> 3)
  }

  "printOrLog" should "print to stdout about the built log content or use logger(BaseLogger) to do a log action if the parameter logger is fulfilled" in {
    val stream = new ByteArrayOutputStream()
    Console.withOut(stream) {
      gangfunctions.printOrLog("content", LogLevel.TRACE)
    }
    stream.toString() should fullyMatch regex traceLog.format(": content")

    stream.reset()
    Console.withOut(stream) {
      gangfunctions.printOrLog("content", LogLevel.TRACE)(GangLogger(isDTEnabled = false))
    }
    stream.toString() should fullyMatch regex traceLog.format(": content")
  }


  "retry" should "invoke a function with tolerance of n times failure and printOrLog when it did" in {
    gangfunctions.retry(3)(1 + 1) match {
      case Failure(_) =>
      case Success(v) => v shouldBe 2
    }

    val stream = new ByteArrayOutputStream()
    Console.withOut(stream) {
      gangfunctions.retry(3)(1 / 0)
    }
    stream.toString() should fullyMatch regex
      errorLog.format(s": ${getRB.getString("retry.failure").format(2, "java.lang.ArithmeticException: / by zero")}") +
        errorLog.format(s": ${getRB.getString("retry.failure").format(1, "java.lang.ArithmeticException: / by zero")}")
  }

  implicit val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

  val workingDirectory: Path = {
    val getFS = PrivateMethod[FileSystem]('getFS)
    val fs = gangfunctions invokePrivate getFS(spark)
    fs.getWorkingDirectory
  }

  val sparkJobDirPath = new Path(workingDirectory, "src/test/resources/spark_job_dir")
  val doesNotExistsDirPath = new Path(workingDirectory, "src/test/resources/does_not_exists")

  "getFS" should "get you a hadoop file system object" in {
    val getFS = PrivateMethod[FileSystem]('getFS)
    val fs = gangfunctions invokePrivate getFS(spark)
    fs.getHomeDirectory.toString should startWith("file:/")
  }

  "isPathExists" should "test a string path existence" in {
    val sparkJobDirStringPath = sparkJobDirPath.toString
    val doesNotExistsDirStringPath = doesNotExistsDirPath.toString
    gangfunctions.isPathExists(sparkJobDirStringPath) shouldBe true
    gangfunctions.isPathExists(doesNotExistsDirStringPath) shouldBe false
  }

  it should "test a Path existence" in {
    gangfunctions.isPathExists(sparkJobDirPath) shouldBe true
    gangfunctions.isPathExists(doesNotExistsDirPath) shouldBe false
  }

  "fileModifiedTime" should "get mtime of string path" in {
    gangfunctions.fileModifiedTime(sparkJobDirPath.toString) match {
      case Left(_) =>
      case Right(t) => t shouldBe >=(0L)
    }
    gangfunctions.fileModifiedTime(doesNotExistsDirPath.toString) match {
      case Left(e) => a[PathNotFoundException] should be thrownBy (throw e)
      case Right(_) =>
    }
  }

  it should "get mtime of Path" in {
    gangfunctions.fileModifiedTime(sparkJobDirPath) match {
      case Left(_) =>
      case Right(t) => t shouldBe >=(0L)
    }
    gangfunctions.fileModifiedTime(doesNotExistsDirPath) match {
      case Left(e) => a[PathNotFoundException] should be thrownBy (throw e)
      case Right(_) =>
    }
  }

  "isSparkSaveDirExists" should "test a spark save dir string path existence" in {
    gangfunctions.isSparkSaveDirExists(sparkJobDirPath.toString) shouldBe true
    gangfunctions.isSparkSaveDirExists(doesNotExistsDirPath.toString) shouldBe false
  }

  "isSparkSaveDirModifiedToday" should "test a spark save dir whether modified today" in {
    val getFS = PrivateMethod[FileSystem]('getFS)
    val fs = gangfunctions invokePrivate getFS(spark)
    val todayTimeMill = LocalDateTime.now()
      .atZone(ZoneId.systemDefault())
      .toInstant.toEpochMilli
    fs.setTimes(new Path(sparkJobDirPath, "_SUCCESS"), todayTimeMill, todayTimeMill)
    gangfunctions.isSparkSaveDirModifiedToday(sparkJobDirPath.toString) shouldBe true

    val yesterdayTimeMill = LocalDateTime.now()
      .atZone(ZoneId.systemDefault())
      .minusDays(1).minusHours(1)
      .toInstant.toEpochMilli
    fs.setTimes(new Path(sparkJobDirPath, "_SUCCESS"), yesterdayTimeMill, yesterdayTimeMill)
    gangfunctions.isSparkSaveDirModifiedToday(sparkJobDirPath.toString) shouldBe false

    a[GangException] should be thrownBy gangfunctions.isSparkSaveDirModifiedToday(doesNotExistsDirPath.toString)
  }

  "isSparkSaveDirModifiedWithinNHours" should "test a spark save dir whether modified within n hours" in {
    val getFS = PrivateMethod[FileSystem]('getFS)
    val fs = gangfunctions invokePrivate getFS(spark)
    val twoHoursAgoTimeMill = LocalDateTime.now()
      .atZone(ZoneId.systemDefault())
      .minusHours(2)
      .toInstant.toEpochMilli
    fs.setTimes(new Path(sparkJobDirPath, "_SUCCESS"), twoHoursAgoTimeMill, twoHoursAgoTimeMill)
    gangfunctions.isSparkSaveDirModifiedWithinNHours(sparkJobDirPath.toString)(3) shouldBe true
    gangfunctions.isSparkSaveDirModifiedWithinNHours(sparkJobDirPath.toString)(1) shouldBe false

    a[GangException] should be thrownBy gangfunctions.isSparkSaveDirModifiedWithinNHours(doesNotExistsDirPath.toString)(1)
  }

}
