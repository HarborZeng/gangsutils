package cn.tellyouwhat.gangsutils.common.helper

import cn.tellyouwhat.gangsutils.common.helper.chaining.TapIt
import java.util.Properties
import scala.collection.JavaConverters._

object ConfigReader {

  private var gangConfig: Option[Map[String, String]] = None

  def getGangConfig: Map[String, String] = {
    gangConfig match {
      case Some(m) => m
      case None =>
        val stream = getClass.getResourceAsStream("/gangConfig.properties")
        val properties = new Properties() |! (_.load(stream))
        stream.close()
        properties.asScala.toMap |! (p => gangConfig = Some(p))
    }
  }

}
