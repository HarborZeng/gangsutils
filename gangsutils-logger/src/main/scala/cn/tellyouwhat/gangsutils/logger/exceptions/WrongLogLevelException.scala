package cn.tellyouwhat.gangsutils.logger.exceptions

import cn.tellyouwhat.gangsutils.core.exceptions.GangException

class WrongLogLevelException(override val optionMessage: Option[String],
                             override val optionCause: Option[Throwable],
                             override val isEnableSuppression: Boolean,
                             override val isWritableStackTrace: Boolean
                            ) extends GangException(optionMessage, optionCause, isEnableSuppression, isWritableStackTrace)

/**
 * WrongLogLevelException who extends RuntimeException
 */
object WrongLogLevelException {
  /**
   * 生成一个新的 WrongLogLevelException
   *
   * @return 一个新的 WrongLogLevelException
   */
  def apply: WrongLogLevelException = WrongLogLevelException()

  /**
   * 使用错误信息生成一个新的 WrongLogLevelException
   *
   * @param message 错误信息
   * @return 一个新的 WrongLogLevelException
   */
  def apply(message: String): WrongLogLevelException = WrongLogLevelException(optionMessage = Some(message))

  /**
   * 使用异常生成一个新的 WrongLogLevelException
   *
   * @param cause 异常
   * @return 一个新的 WrongLogLevelException
   */
  def apply(cause: Throwable): WrongLogLevelException = WrongLogLevelException(optionCause = Some(cause))

  /**
   * 使用错误信息、异常、isEnableSuppression、isWritableStackTrace 生成一个新的 WrongLogLevelException
   *
   * @param optionMessage        Option[错误信息]
   * @param optionCause          Option[异常]
   * @param isEnableSuppression  whether or not suppression is enabled or disabled
   * @param isWritableStackTrace whether or not the stack trace should be writable
   * @return 一个新的 WrongLogLevelException
   */
  def apply(optionMessage: Option[String] = None,
            optionCause: Option[Throwable] = None,
            isEnableSuppression: Boolean = false,
            isWritableStackTrace: Boolean = false): WrongLogLevelException =
    new WrongLogLevelException(optionMessage, optionCause, isEnableSuppression, isWritableStackTrace)

  /**
   * 使用错误信息和异常生成一个新的 WrongLogLevelException
   *
   * @param message 错误信息
   * @param cause   异常
   * @return 一个新的 WrongLogLevelException
   */
  def apply(message: String, cause: Throwable): WrongLogLevelException = WrongLogLevelException(optionMessage = Some(message), optionCause = Some(cause))
}