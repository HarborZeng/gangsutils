package cn.tellyouwhat.gangsutils.common.exceptions

class NoAliveLoggerException(override val optionMessage: Option[String],
                             override val optionCause: Option[Throwable],
                             override val isEnableSuppression: Boolean,
                             override val isWritableStackTrace: Boolean
                            ) extends GangException(optionMessage, optionCause, isEnableSuppression, isWritableStackTrace)

/**
 * NoAliveLoggerException who extends RuntimeException
 */
object NoAliveLoggerException {
  /**
   * 生成一个新的 NoAliveLoggerException
   *
   * @return 一个新的 NoAliveLoggerException
   */
  def apply: NoAliveLoggerException = NoAliveLoggerException()

  /**
   * 使用错误信息生成一个新的 NoAliveLoggerException
   *
   * @param message 错误信息
   * @return 一个新的 NoAliveLoggerException
   */
  def apply(message: String): NoAliveLoggerException = NoAliveLoggerException(optionMessage = Some(message))

  /**
   * 使用异常生成一个新的 NoAliveLoggerException
   *
   * @param cause 异常
   * @return 一个新的 NoAliveLoggerException
   */
  def apply(cause: Throwable): NoAliveLoggerException = NoAliveLoggerException(optionCause = Some(cause))

  /**
   * 使用错误信息和异常生成一个新的 NoAliveLoggerException
   *
   * @param message 错误信息
   * @param cause   异常
   * @return 一个新的 NoAliveLoggerException
   */
  def apply(message: String, cause: Throwable): NoAliveLoggerException = NoAliveLoggerException(optionMessage = Some(message), optionCause = Some(cause))

  /**
   * 使用错误信息、异常、isEnableSuppression、isWritableStackTrace 生成一个新的 NoAliveLoggerException
   *
   * @param optionMessage        Option[错误信息]
   * @param optionCause          Option[异常]
   * @param isEnableSuppression  whether or not suppression is enabled or disabled
   * @param isWritableStackTrace whether or not the stack trace should be writable
   * @return 一个新的 NoAliveLoggerException
   */
  def apply(optionMessage: Option[String] = None,
            optionCause: Option[Throwable] = None,
            isEnableSuppression: Boolean = false,
            isWritableStackTrace: Boolean = false): NoAliveLoggerException =
    new NoAliveLoggerException(optionMessage, optionCause, isEnableSuppression, isWritableStackTrace)
}