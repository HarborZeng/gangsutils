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

/**
 * object ConfigReader for accessing configuration from underlying variables or classpath files
 */
object ConfigReader {

  /**
   * properties configuration map
   */
  private[helper] var gangPropertiesConfig: Option[Map[String, String]] = None

  /**
   * yaml configuration Json
   */
  private[helper] var gangYamlConfig: Option[Json] = None

  /**
   * json configuration Json
   */
  private[helper] var gangJsonConfig: Option[Json] = None

  /**
   * get from member variable or create properties configuration from classpath file gangsutilsConfig.properties
   * @return properties configuration map
   */
  def getGangPropertiesConfig: Map[String, String] = {
    gangPropertiesConfig match {
      case Some(m) => m
      case None =>
        getPropertiesFromClassPath("gangsutilsConfig.properties")
          .tap(p => gangPropertiesConfig = Some(p))
    }
  }

  /**
   * get from member variable or create yaml configuration from classpath file gangsutilsConfig.yaml
   * @return yaml configuration Json
   */
  def getGangYamlConfig: Json = {
    gangYamlConfig match {
      case Some(m) => m
      case None =>
        getYamlFromClassPath("gangsutilsConfig.yaml")
          .tap(p => gangYamlConfig = Some(p))
    }
  }

  /**
   * get from member variable or create Json configuration from classpath file gangsutilsConfig.json
   * @return json configuration Json
   */
  def getGangJsonConfig: Json = {
    gangJsonConfig match {
      case Some(m) => m
      case None =>
        getJsonFromClassPath("gangsutilsConfig.json")
          .tap(p => gangJsonConfig = Some(p))
    }
  }

  /**
   * map of [file to Json] for storing Json objects
   */
  private val filesInJsonFormat = mutable.Map.empty[String, Json]

  /**
   * get Json object from the underlying filesInJsonFormat or read from classpath
   * @param path the path as key to look for configuration
   * @return the Json configuration object
   */
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

  /**
   * map of [file to Json] for storing Json objects
   */
  private val filesInYamlFormat = mutable.Map.empty[String, Json]

  /**
   * get Json object from the underlying filesInYamlFormat or read from classpath
   * @param path the path as key to look for configuration
   * @return the Json configuration object
   */
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

  /**
   * Map[String, Map[String, String]] for storing Json objects
   */
  private val filesInPropertiesFormat = mutable.Map.empty[String, Map[String, String]]

  /**
   * get Json object from the underlying filesInPropertiesFormat or read from classpath
   * @param path the path as key to look for configuration
   * @return the Json configuration object
   */
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
