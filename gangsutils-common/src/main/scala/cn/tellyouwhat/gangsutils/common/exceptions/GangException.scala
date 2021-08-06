package cn.tellyouwhat.gangsutils.common.exceptions

object GangException {
  /**
   * 生成一个新的 GangException
   *
   * @return 一个新的 GangException
   */
  def apply: GangException = GangException()

  /**
   * 使用错误信息生成一个新的 GangException
   *
   * @param message 错误信息
   * @return 一个新的 GangException
   */
  def apply(message: String): GangException = GangException(optionMessage = Some(message))

  /**
   * 使用异常生成一个新的 GangException
   *
   * @param cause 异常
   * @return 一个新的 GangException
   */
  def apply(cause: Throwable): GangException = GangException(optionCause = Some(cause))

  /**
   * 使用错误信息和异常生成一个新的 GangException
   *
   * @param message 错误信息
   * @param cause   异常
   * @return 一个新的 GangException
   */
  def apply(message: String, cause: Throwable): GangException = GangException(optionMessage = Some(message), optionCause = Some(cause))

  /**
   * 使用错误信息、异常、isEnableSuppression、isWritableStackTrace 生成一个新的 GangException
   *
   * @param optionMessage        Option[错误信息]
   * @param optionCause          Option[异常]
   * @param isEnableSuppression  whether or not suppression is enabled or disabled
   * @param isWritableStackTrace whether or not the stack trace should be writable
   * @return 一个新的 GangException
   */
  def apply(optionMessage: Option[String] = None,
            optionCause: Option[Throwable] = None,
            isEnableSuppression: Boolean = false,
            isWritableStackTrace: Boolean = false): GangException =
    new GangException(optionMessage, optionCause, isEnableSuppression, isWritableStackTrace)
}

/**
 * @inheritdoc
 */
class GangException private[exceptions](val optionMessage: Option[String],
                                        val optionCause: Option[Throwable],
                                        val isEnableSuppression: Boolean,
                                        val isWritableStackTrace: Boolean
                                       ) extends RuntimeException(
  optionMessage match {
    case Some(string) => string
    case None => null
  },
  optionCause match {
    case Some(throwable) => throwable
    case None => null
  },
  isEnableSuppression,
  isWritableStackTrace
)