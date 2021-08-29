package cn.tellyouwhat.gangsutils.core.helper

import cn.tellyouwhat.gangsutils.core.exceptions.GangException
import cn.tellyouwhat.gangsutils.core.helper.chaining.TapIt
import io.circe.Json
import io.circe.parser.{parse => jsonParse}
import io.circe.yaml.parser.{parse => yamlParse}

import java.util.Properties
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.io.Source
import scala.util.{Failure, Success, Try}

object ConfigReader {

  private[helper] var gangPropertiesConfig: Option[Map[String, String]] = None
  private[helper] var gangYamlConfig: Option[Json] = None
  private[helper] var gangJsonConfig: Option[Json] = None

  def getGangPropertiesConfig: Map[String, String] = {
    gangPropertiesConfig match {
      case Some(m) => m
      case None =>
        getPropertiesFromClassPath("gangsutilsConfig.properties")
          .tap(p => gangPropertiesConfig = Some(p))
    }
  }

  def getGangYamlConfig: Json = {
    gangYamlConfig match {
      case Some(m) => m
      case None =>
        getYamlFromClassPath("gangsutilsConfig.yaml")
          .tap(p => gangYamlConfig = Some(p))
    }
  }

  def getGangJsonConfig: Json = {
    gangJsonConfig match {
      case Some(m) => m
      case None =>
        getJsonFromClassPath("gangsutilsConfig.json")
          .tap(p => gangJsonConfig = Some(p))
    }
  }

  private val filesInJsonFormat = mutable.Map.empty[String, Json]

  def getJsonFromClassPath(path: String): Json = {
    if (filesInJsonFormat.keySet.contains(path))
      filesInJsonFormat(path)
    else {
      val source = Source.fromResource(path)
      Try(source.mkString) match {
        case Failure(e) => throw GangException(s"error when reading from classpath $path, make sure path exists", e)
        case Success(jsonString) =>
          jsonParse(jsonString) match {
            case Left(e) => throw GangException(s"error when parsing $jsonString", e)
            case Right(j) => j |! (j => filesInJsonFormat += (path -> j)) |! (_ => source.close())
          }
      }
    }
  }

  private val filesInYamlFormat = mutable.Map.empty[String, Json]

  def getYamlFromClassPath(path: String): Json = {
    if (filesInYamlFormat.keySet.contains(path))
      filesInYamlFormat(path)
    else {
      val source = Source.fromResource(path)
      Try(source.reader()) match {
        case Failure(e) => throw GangException(s"error when reading from classpath $path, make sure path exists", e)
        case Success(reader) =>
          yamlParse(reader) match {
            case Left(e) => throw GangException(s"error when parsing ${source.mkString}", e)
            case Right(j) => j |! (j => filesInYamlFormat += (path -> j)) |! (_ => reader.close()) |! (_ => source.close())
          }
      }
    }
  }

  private val filesInPropertiesFormat = mutable.Map.empty[String, Map[String, String]]

  def getPropertiesFromClassPath(path: String): Map[String, String] = {
    if (filesInPropertiesFormat.keySet.contains(path))
      filesInPropertiesFormat(path)
    else {
      val source = Source.fromResource(path)
      Try(source.reader()) match {
        case Failure(e) => throw GangException(s"error when reading from classpath $path, make sure path exists", e)
        case Success(reader) =>
          new Properties()
            .tap(_.load(reader))
            .tap(_ => {
              reader.close()
              source.close()
            })
            .asScala
            .toMap
            .tap(p => filesInPropertiesFormat += (path -> p))
      }
    }
  }

}
