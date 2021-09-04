package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.logger.LoggerCompanion

trait WebhookLoggerCompanion extends LoggerCompanion {
  /**
   * proxy host
   */
  private[logger] var proxyHost: String = ""

  /**
   * proxy port
   */
  private[logger] var proxyPort: Int = -1

  /**
   * set up proxy
   *
   * @param host proxy host
   * @param port proxy port
   */
  def setProxy(host: String, port: Int): Unit = {
    proxyHost = host
    proxyPort = port
  }

  /**
   * clear the proxy
   */
  def clearProxy(): Unit = {
    proxyHost = ""
    proxyPort = -1
  }
}
