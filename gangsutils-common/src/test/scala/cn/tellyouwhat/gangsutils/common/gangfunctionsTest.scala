package cn.tellyouwhat.gangsutils.common

import cn.tellyouwhat.gangsutils.common.cc.Mappable
import cn.tellyouwhat.gangsutils.common.logger.{GangLogger, LogLevel}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.ByteArrayOutputStream
import scala.util.{Failure, Success}

class gangfunctionsTest extends AnyFlatSpec with Matchers {

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


  "timeit" should "time a function invocation and log the start and execution duration" in {
    val stream = new ByteArrayOutputStream()
    Console.withOut(stream) {
      gangfunctions.timeit(1 + 1) shouldBe 2
    }
    stream.toString() should fullyMatch regex """【跟踪】: 开始任务\s+【成功】: 完成任务，耗时\d+\.*\d*s\s+""".r

    stream.reset()
    Console.withOut(stream) {
      a [ArithmeticException] should be thrownBy {
        gangfunctions.timeit(1 / 0)
      }
    }
    stream.toString() should fullyMatch regex """【跟踪】: 开始任务\s+【致命】: 执行任务失败，耗时\d+\.*\d*s\s+""".r
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
    stream.toString() should fullyMatch regex  """【跟踪】: content\s+""".r

    stream.reset()
    Console.withOut(stream) {
      gangfunctions.printOrLog("content", LogLevel.TRACE)(GangLogger(isDTEnabled = false))
    }
    stream.toString() should fullyMatch regex  """【跟踪】: content\s+""".r
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
      """【错误】: 执行失败，重试最后2次，error: java.lang.ArithmeticException: / by zero\s+【错误】: 执行失败，重试最后1次，error: java.lang.ArithmeticException: / by zero\s+""".r
  }

}
