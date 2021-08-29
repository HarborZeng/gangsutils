package cn.tellyouwhat.gangsutils.logger.exceptions

import cn.tellyouwhat.gangsutils.core.exceptions.GangException

class WrongHttpMethodException(override val optionMessage: Option[String],
                               override val optionCause: Option[Throwable],
                               override val isEnableSuppression: Boolean,
                               override val isWritableStackTrace: Boolean
                              ) extends GangException(optionMessage, optionCause, isEnableSuppression, isWritableStackTrace)

/**
 * WrongHttpMethodException who extends RuntimeException
 */
object WrongHttpMethodException {
  /**
   * 生成一个新的 WrongHttpMethodException
   *
   * @return 一个新的 WrongHttpMethodException
   */
  def apply: WrongHttpMethodException = WrongHttpMethodException()

  /**
   * 使用错误信息生成一个新的 WrongHttpMethodException
   *
   * @param message 错误信息
   * @return 一个新的 WrongHttpMethodException
   */
  def apply(message: String): WrongHttpMethodException = WrongHttpMethodException(optionMessage = Some(message))

  /**
   * 使用异常生成一个新的 WrongHttpMethodException
   *
   * @param cause 异常
   * @return 一个新的 WrongHttpMethodException
   */
  def apply(cause: Throwable): WrongHttpMethodException = WrongHttpMethodException(optionCause = Some(cause))

  /**
   * 使用错误信息、异常、isEnableSuppression、isWritableStackTrace 生成一个新的 WrongHttpMethodException
   *
   * @param optionMessage        Option[错误信息]
   * @param optionCause          Option[异常]
   * @param isEnableSuppression  whether or not suppression is enabled or disabled
   * @param isWritableStackTrace whether or not the stack trace should be writable
   * @return 一个新的 WrongHttpMethodException
   */
  def apply(optionMessage: Option[String] = None,
            optionCause: Option[Throwable] = None,
            isEnableSuppression: Boolean = false,
            isWritableStackTrace: Boolean = false): WrongHttpMethodException =
    new WrongHttpMethodException(optionMessage, optionCause, isEnableSuppression, isWritableStackTrace)

  /**
   * 使用错误信息和异常生成一个新的 WrongHttpMethodException
   *
   * @param message 错误信息
   * @param cause   异常
   * @return 一个新的 WrongHttpMethodException
   */
  def apply(message: String, cause: Throwable): WrongHttpMethodException = WrongHttpMethodException(optionMessage = Some(message), optionCause = Some(cause))
}