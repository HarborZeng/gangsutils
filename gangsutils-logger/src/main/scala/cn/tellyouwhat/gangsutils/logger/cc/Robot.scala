package cn.tellyouwhat.gangsutils.logger.cc

/**
 * Robot case class for DingTalk and Feishu webhook logger
 *
 * @param token webhook token
 * @param sign  webhook sign secret
 */
case class Robot(token: Option[String], sign: Option[String])

/**
 * Telegram Robot
 *
 * @param chatID telegram chatID
 * @param token  telegram webhook token
 */
case class TelegramRobot(chatID: Option[String], token: Option[String])
