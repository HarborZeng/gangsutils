package cn.tellyouwhat.gangsutils.core.helper

import cn.tellyouwhat.gangsutils.core.helper.ConfigReader.getGangYamlConfig
import cn.tellyouwhat.gangsutils.core.helper.chaining.TapIt

import java.util.{Locale, ResourceBundle}

/**
 * Internationalization object for multi-lang
 */
private[gangsutils] object I18N {

  /**
   * the option object of resource bundle to store classpath file gangsutils_LANG_COUNTRY.properties
   */
  private var rbo: Option[ResourceBundle] = None

  /**
   * get resource bundle object from the underlying variable rbo, or read from classpath
   * @return
   */
  def getRB: ResourceBundle = {
    rbo match {
      case Some(rb) => rb
      case None =>
        val config = getGangYamlConfig
        val lang = config.hcursor.downField("logger").downField("lang").as[String] match {
          case Left(e) =>
            println(s"key logger.lang was not found in config file: $e")
            val locale = Locale.getDefault()
            if (locale.getCountry == "TW" || locale.getCountry == "HK" || locale.getCountry == "MO")
              "zh-hant"
            else if (locale.getCountry == "CN" || locale.getCountry == "SG" || locale.getCountry == "MY")
              "zh-hans"
            else
              locale.getLanguage
          case Right(value) => value
        }
        ResourceBundle.getBundle("gangsutils", new Locale(lang)) |! (rb => rbo = Some(rb))
    }
  }

  /**
   * set rbo to None
   */
  def clearRB(): Unit = rbo = None
}
