package cn.tellyouwhat.gangsutils.useless

import cn.tellyouwhat.gangsutils.logger.GangLogger

object AnotherJob {
  def job001(): Unit = {
    val logger = GangLogger.getLogger
    logger.info("a info log")
  }
}
