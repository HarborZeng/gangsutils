package cn.tellyouwhat.gangsutils.core.helper

import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import io.circe.Json
import io.circe.parser.{parse => jsonParse}
import io.circe.yaml.parser.{parse => yamlParse}

import java.io.InputStreamReader
import java.util.Properties
import scala.collection.JavaConverters._

object ConfigReader {

  private[helper] var gangPropertiesConfig: Option[Map[String, String]] = None
  private[helper] var gangYamlConfig: Option[Json] = None
  private[helper] var gangJsonConfig: Option[Json] = None

  def getGangPropertiesConfig: Map[String, String] = {
    gangPropertiesConfig match {
      case Some(m) => m
      case None =>
        val stream = getClass.getResourceAsStream("/gangsutilsConfig.properties")
        val properties = new Properties() |! (_.load(stream))
        stream.close()
        properties.asScala.toMap |! (p => gangPropertiesConfig = Some(p))
    }
  }

  def getGangYamlConfig: Json = {
    gangYamlConfig match {
      case Some(m) => m
      case None =>
        val stream = getClass.getResourceAsStream("/gangsutilsConfig.yaml")
        new InputStreamReader(stream) |> yamlParse match {
          case Left(e) => throw e
          case Right(value) => value |! (p => gangYamlConfig = Some(p))
        }

    }
  }

  def getGangJsonConfig: Json = {
    gangJsonConfig match {
      case Some(m) => m
      case None =>
        val stream = getClass.getResourceAsStream("/gangsutilsConfig.json")
        new String(stream.readAllBytes()) |> jsonParse match {
          case Left(e) => throw e
          case Right(value) => value |! (p => gangJsonConfig = Some(p))
        }
    }
  }

}
