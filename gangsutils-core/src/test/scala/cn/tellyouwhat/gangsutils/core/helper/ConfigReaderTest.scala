package cn.tellyouwhat.gangsutils.core.helper

import io.circe.{FailedCursor, HCursor}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConfigReaderTest extends AnyFlatSpec with Matchers {

  behavior of "ConfigReaderTest"

  "getGangJsonConfig" should "get gangsutilsConfig.json configuration at first time" in {
    val config = ConfigReader.getGangJsonConfig
    config.hcursor.downField("logger") match {
      case _: FailedCursor =>
      case cursor: HCursor =>
        cursor.downField("lang") match {
          case _: FailedCursor =>
          case cursor: HCursor => cursor.as[String] match {
            case Left(_) =>
            case Right(lang) => lang shouldBe "zh-hans"
          }
          case _ =>
        }
        cursor.downField("notExists") match {
          case cursor: FailedCursor => cursor.failed shouldBe true
          case _: HCursor =>
          case _ =>
        }
      case _ =>
    }
  }

  it should "get gangsutilsConfig.json configuration at latter times" in {
    //set variable to None
    ConfigReader.gangJsonConfig = None
    ConfigReader.gangJsonConfig shouldBe None
    //create and set variable
    ConfigReader.getGangJsonConfig
    //variable is not None now
    ConfigReader.gangJsonConfig shouldNot be(None)
    //get config from variable
    val config2 = ConfigReader.getGangJsonConfig
    //config should equal to variable
    config2 shouldBe ConfigReader.gangJsonConfig.get
  }

  "getGangYamlConfig" should "get gangsutilsConfig.yaml configuration at first time" in {
    val config = ConfigReader.getGangYamlConfig
    config.hcursor.downField("logger") match {
      case _: FailedCursor =>
      case cursor: HCursor =>
        cursor.downField("lang") match {
          case _: FailedCursor =>
          case cursor: HCursor => cursor.as[String] match {
            case Left(_) =>
            case Right(lang) => lang shouldBe "zh-hans"
          }
          case _ =>
        }
        cursor.downField("notExists") match {
          case cursor: FailedCursor => cursor.failed shouldBe true
          case _: HCursor =>
          case _ =>
        }
      case _ =>
    }
  }

  it should "get gangsutilsConfig.yaml configuration at latter times" in {
    //set variable to None
    ConfigReader.gangYamlConfig = None
    ConfigReader.gangYamlConfig shouldBe None
    //create and set variable
    ConfigReader.getGangYamlConfig
    //variable is not None now
    ConfigReader.gangYamlConfig shouldNot be(None)
    //get config from variable
    val config2 = ConfigReader.getGangYamlConfig
    //config should equal to variable
    config2 shouldBe ConfigReader.gangYamlConfig.get
  }

  "getGangPropertiesConfig" should "get gangsutilsConfig.properties configuration at first time" in {
    val config = ConfigReader.getGangPropertiesConfig
    config should contain theSameElementsAs Map(
      "logger.lang" -> "zh-hans",
      "logger.timeZone" -> "+8",
    )
  }

  it should "get gangsutilsConfig.properties configuration at latter times" in {
    //set variable to None
    ConfigReader.gangPropertiesConfig = None
    ConfigReader.gangPropertiesConfig shouldBe None
    //create and set variable
    ConfigReader.getGangPropertiesConfig
    //variable is not None now
    ConfigReader.gangPropertiesConfig shouldNot be(None)
    //get config from variable
    val config2 = ConfigReader.getGangPropertiesConfig
    //config should equal to variable
    config2 shouldBe ConfigReader.gangPropertiesConfig.get
  }

}
