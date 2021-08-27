package cn.tellyouwhat.gangsutils.core.helper

import io.circe.{Json, JsonObject}
import org.mockito.IdiomaticMockito.StubbingOps
import org.mockito.MockitoSugar
import org.mockito.scalatest.ResetMocksAfterEachTest
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.Locale

class I18NTest extends AnyFlatSpec with Matchers with MockitoSugar with BeforeAndAfter with ResetMocksAfterEachTest {

  after {
    I18N.clearRB()
  }
  before {
    I18N.clearRB()
  }

  behavior of "I18NTest"

  "getRB" should "get tw hant resource bundle" in {
    Locale.setDefault(Locale.TAIWAN)
    withObjectMocked[ConfigReader.type] {
      ConfigReader.getGangYamlConfig returns Json.Null
      val b = I18N.getRB
      b.getString("logLevel.info") shouldBe "訊息"
    }
  }
  it should "get hk hant resource bundle" in {
    Locale.setDefault(new Locale("zh", "HK"))
    withObjectMocked[ConfigReader.type] {
      ConfigReader.getGangYamlConfig returns Json.Null
      val b = I18N.getRB
      b.getString("logLevel.info") shouldBe "訊息"
    }
  }
  it should "get mo hant resource bundle" in {
    Locale.setDefault(new Locale("zh", "MO"))
    withObjectMocked[ConfigReader.type] {
      ConfigReader.getGangYamlConfig returns Json.Null
      val b = I18N.getRB
      b.getString("logLevel.info") shouldBe "訊息"
    }
  }
  it should "get cn hans resource bundle" in {
    Locale.setDefault(new Locale("zh", "CN"))
    withObjectMocked[ConfigReader.type] {
      ConfigReader.getGangYamlConfig returns Json.Null
      val b = I18N.getRB
      b.getString("logLevel.info") shouldBe "信息"
    }
  }
  it should "get sg hans resource bundle" in {
    Locale.setDefault(new Locale("zh", "SG"))
    withObjectMocked[ConfigReader.type] {
      ConfigReader.getGangYamlConfig returns Json.Null
      val b = I18N.getRB
      b.getString("logLevel.info") shouldBe "信息"
    }
  }
  it should "get my hans resource bundle" in {
    Locale.setDefault(new Locale("zh", "MY"))
    withObjectMocked[ConfigReader.type] {
      ConfigReader.getGangYamlConfig returns Json.Null
      val b = I18N.getRB
      b.getString("logLevel.info") shouldBe "信息"
    }
  }
  it should "get de resource bundle" in {
    Locale.setDefault(Locale.GERMANY)
    withObjectMocked[ConfigReader.type] {
      ConfigReader.getGangYamlConfig returns Json.Null
      val b = I18N.getRB
      b.getString("logLevel.info") shouldBe "INFO"
    }
  }
  it should "get resource bundle from reading /gangsutilsConfig.yaml (mocked)" in {
    withObjectMocked[ConfigReader.type] {
      ConfigReader.getGangYamlConfig returns Json.fromJsonObject(
        JsonObject("logger" -> Json.fromJsonObject(JsonObject(
          "lang" -> Json.fromString("zh-hans"),
          "timeZone" -> Json.fromString("+8"),
        )))
      )
      I18N.getRB
      val b = I18N.getRB
      b.getString("logLevel.info") shouldBe "信息"
    }
  }

}
