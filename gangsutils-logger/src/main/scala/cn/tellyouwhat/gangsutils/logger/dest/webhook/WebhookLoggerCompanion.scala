package cn.tellyouwhat.gangsutils.logger.dest.webhook

import cn.tellyouwhat.gangsutils.logger.LoggerCompanion

trait WebhookLoggerCompanion extends LoggerCompanion {
  /**
   * proxy host
   */
  private[webhook] var proxyHost: Option[String] = None

  /**
   * proxy port
   */
  private[webhook] var proxyPort: Option[Int] = None

  /**
   * set up proxy
   *
   * @param host proxy host
   * @param port proxy port
   */
  def setProxy(host: String, port: Int): Unit = {
    proxyHost = Some(host)
    proxyPort = Some(port)
  }

  /**
   * clear the proxy
   */
  def clearProxy(): Unit = {
    proxyHost = None
    proxyPort = None
  }
}
