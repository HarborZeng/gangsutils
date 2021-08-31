package cn.tellyouwhat.gangsutils.core

import cn.tellyouwhat.gangsutils.core.cc.Mappable
import cn.tellyouwhat.gangsutils.core.helper.I18N.getRB
import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}

import java.io.ByteArrayOutputStream
import scala.Console.RED
import scala.util.{Failure, Success}

class funcsTest extends AnyFlatSpec with Matchers with PrivateMethodTester with BeforeAndAfter {
  val stream = new ByteArrayOutputStream()

  after {
    stream.reset()
  }
  "reduceByKey" should "group and for each group do a reduce by key" in {
    val ext_1 = Array("txt", "txt", "pdf", "docx", "docx", "ppt", "ppt", "ppt").map((_, 1))
    val ext_count = funcs.reduceByKey(ext_1)
    ext_count should have size 4
    ext_count should contain allOf("txt" -> 2, "pdf" -> 1, "docx" -> 2, "ppt" -> 3)
  }

  "endWithET" should "test a file whether ends with et extensions" in {
    funcs.endWithET("abc.et") shouldBe true
    funcs.endWithET("abc.xls") shouldBe true
    funcs.endWithET("abc.xlsx") shouldBe true
    funcs.endWithET("abc") shouldBe false
    funcs.endWithET("") shouldBe false
    a[NullPointerException] should be thrownBy funcs.endWithET(null)
  }

  "endWithTxt" should "test a file whether ends with txt extension" in {
    funcs.endWithTxt("abc.txt") shouldBe true
    funcs.endWithTxt("abc") shouldBe false
    funcs.endWithTxt("") shouldBe false
    a[NullPointerException] should be thrownBy funcs.endWithTxt(null)
  }

  "endWithWPS" should "test a file whether ends with wps extensions" in {
    funcs.endWithWPS("abc.doc") shouldBe true
    funcs.endWithWPS("abc.docx") shouldBe true
    funcs.endWithWPS("abc.wps") shouldBe true
    funcs.endWithWPS("abc") shouldBe false
    funcs.endWithWPS("") shouldBe false
    a[NullPointerException] should be thrownBy funcs.endWithWPS(null)
  }

  "endWithWPP" should "test a file whether ends with wpp extensions" in {
    funcs.endWithWPP("abc.ppt") shouldBe true
    funcs.endWithWPP("abc.pptx") shouldBe true
    funcs.endWithWPP("abc.wpp") shouldBe true
    funcs.endWithWPP("abc") shouldBe false
    funcs.endWithWPP("") shouldBe false
    a[NullPointerException] should be thrownBy funcs.endWithWPP(null)
  }

  "endWithImage" should "test a file whether ends with image extensions" in {
    funcs.endWithImage("abc.jpg") shouldBe true
    funcs.endWithImage("abc.jpeg") shouldBe true
    funcs.endWithImage("abc.png") shouldBe true
    funcs.endWithImage("abc.bmp") shouldBe true
    funcs.endWithImage("abc.gif") shouldBe true
    funcs.endWithImage("abc") shouldBe false
    funcs.endWithImage("") shouldBe false
    a[NullPointerException] should be thrownBy funcs.endWithImage(null)
  }

  "endWithPDF" should "test a file whether ends with pdf extensions" in {
    funcs.endWithPDF("abc.pdf") shouldBe true
    funcs.endWithPDF("abc") shouldBe false
    funcs.endWithPDF("") shouldBe false
    a[NullPointerException] should be thrownBy funcs.endWithPDF(null)
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
    case class EntitySimple(v: String) extends Mappable
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
                          v2: Option[EntitySimple],
                          map: Map[String, Double],
                          ssm: List[EntitySimple],
                          sse: List[String],
                          om: Option[EntitySimple],
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
      v2 = None,
      map = Map("c" -> 0.4d, "d" -> 0.5d),
      ssm = List(EntitySimple("simple1"), EntitySimple("simple2")),
      sse = List.empty[String],
      om = Some(EntitySimple("oooommmm")),
      cc = in
    )
    val map = funcs.ccToMap(out)
    map should have size 20
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
    map("v2").asInstanceOf[EntitySimple] should be (null)
    val stringToDouble = map("map").asInstanceOf[Map[String, Double]]
    stringToDouble should have size 2
    stringToDouble should contain allOf("c" -> 0.4d, "d" -> 0.5d)
    map("ssm").asInstanceOf[List[EntitySimple]] should contain theSameElementsAs List(EntitySimple("simple1"), EntitySimple("simple2"))
    map("sse").asInstanceOf[List[String]] should have size 0
    map("om").asInstanceOf[Map[String, String]].toSeq should contain theSameElementsAs Seq("v" -> "oooommmm")
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
    inMap("sss").asInstanceOf[Seq[String]] === Seq("abc", "def")
    inMap("ssl").asInstanceOf[List[String]] === Seq("a", "b")
    inMap("ssa").asInstanceOf[Array[String]] === Seq("c", "d")
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
    funcs.ccToMap(null) shouldBe null
  }

  "retry" should "invoke a function with tolerance of n times failure and printOrLog when it did" in {
    funcs.retry(3)(1 + 1) match {
      case Failure(_) =>
      case Success(v) => v shouldBe 2
    }

    Console.withOut(stream) {
      funcs.retry(3)(1 / 0)
    }
    stream.toString() should fullyMatch regex
      getRB.getString("retry.failure").format(2, "java.lang.ArithmeticException: / by zero") + """\s+""" +
        getRB.getString("retry.failure").format(1, "java.lang.ArithmeticException: / by zero") + """\s+"""
  }

  "stripANSIColor" should "strip ANSI characters from a string" in {
    RED |> funcs.stripANSIColor shouldBe ""
  }

  "calcExecDuration" should "calculate the duration between two millis timestamp" in {
    val t0 = 1630078401328L
    val t1 = 1630078402328L
    funcs.calcExecDuration(t0, t1) shouldBe "1s"
  }
}
