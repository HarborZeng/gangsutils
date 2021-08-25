package cn.tellyouwhat.gangsutils.common.helper

import cn.tellyouwhat.gangsutils.common.helper.ConfigReader.getGangConfig

import java.util.{Locale, ResourceBundle}

private[gangsutils] object I18N {
  private var rbo: Option[ResourceBundle] = None

  def getRB: ResourceBundle = {
    rbo match {
      case Some(rb) => rb
      case None =>
        val rb = if (getGangConfig.contains("default-lang") && getGangConfig.contains("default-region")) {
          val defaultLang = getGangConfig("default-lang")
          val defaultRegion = getGangConfig("default-region")
          ResourceBundle.getBundle("gangsutils", new Locale(defaultLang, defaultRegion))
        } else {
          ResourceBundle.getBundle("gangsutils")
        }
        rbo = Some(rb)
        rb
    }
  }
}
