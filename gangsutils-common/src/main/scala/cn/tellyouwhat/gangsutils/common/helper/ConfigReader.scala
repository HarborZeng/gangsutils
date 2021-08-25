package cn.tellyouwhat.gangsutils.common.helper

import cn.tellyouwhat.gangsutils.common.helper.chaining.{PipeIt, TapIt}
import io.circe.Json

import java.util.Properties
import scala.collection.JavaConverters._
import io.circe.yaml.parser.{parse => yamlParse}
import io.circe.parser.{parse => jsonParse}

import java.io.InputStreamReader

object ConfigReader {

  private var gangPropertiesConfig: Option[Map[String, String]] = None
  private var gangYamlConfig: Option[Json] = None
  private var gangJsonConfig: Option[Json] = None

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
