package cn.tellyouwhat.gangsutils.common.helper

import cn.tellyouwhat.gangsutils.common.helper.ConfigReader.getGangYamlConfig

import java.util.{Locale, ResourceBundle}

private[gangsutils] object I18N {
  private var rbo: Option[ResourceBundle] = None

  def getRB: ResourceBundle = {
    rbo match {
      case Some(rb) => rb
      case None =>
        val config = getGangYamlConfig
        val lang = config.hcursor.downField("logger").downField("lang").as[String] match {
          case Left(e) =>
            println(s"key logger.lang was not found in config file: $e")
            val locale = Locale.getDefault()
            if (locale.getCountry == "TW" || locale.getCountry == "HK" || locale.getCountry == "MO") {
              "zh-hant"
            } else if (locale.getCountry == "CN" || locale.getCountry == "SG" || locale.getCountry == "MY") {
              "zh-hans"
            } else {
              locale.getLanguage
            }
          case Right(value) => value
        }
        val rb = ResourceBundle.getBundle("gangsutils", new Locale(lang))
        rbo = Some(rb)
        rb
    }
  }
}
