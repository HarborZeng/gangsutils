package cn.tellyouwhat.gangsutils.logger.cc

case class Robot(token: Option[String], sign: Option[String])

case class TelegramRobot(chatID: Option[String], token: Option[String])
