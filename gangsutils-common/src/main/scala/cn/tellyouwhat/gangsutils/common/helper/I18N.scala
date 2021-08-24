package cn.tellyouwhat.gangsutils.common.helper

import cn.tellyouwhat.gangsutils.common.helper.ConfigReader.getGangConfig
import cn.tellyouwhat.gangsutils.common.helper.chaining.TapIt

import java.util.{Locale, ResourceBundle}

object I18N {
  private var rbo: Option[ResourceBundle] = None

  def getRB: ResourceBundle = {
    rbo match {
      case Some(rb) => rb
      case None => ResourceBundle.getBundle("gangsutils",
        new Locale(getGangConfig("default-lang"), getGangConfig("default-region"))) |! (rb => rbo = Some(rb))
    }
  }
}
