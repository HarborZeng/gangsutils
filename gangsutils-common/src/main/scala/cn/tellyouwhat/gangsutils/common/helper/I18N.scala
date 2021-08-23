package cn.tellyouwhat.gangsutils.common.helper

import cn.tellyouwhat.gangsutils.common.helper.chaining.TapIt

import java.util.{Locale, ResourceBundle}

object I18N {
  private var rbo: Option[ResourceBundle] = None

  def getRB: ResourceBundle = {
    rbo match {
      case Some(rb) => rb
      case None => ResourceBundle.getBundle("gangsutils", Locale.ENGLISH) |! (rb => rbo = Some(rb))
    }
  }
}
