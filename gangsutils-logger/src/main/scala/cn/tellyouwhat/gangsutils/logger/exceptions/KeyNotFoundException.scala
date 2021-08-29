package cn.tellyouwhat.gangsutils.logger.exceptions

import cn.tellyouwhat.gangsutils.core.exceptions.GangException

class KeyNotFoundException(override val optionMessage: Option[String],
                           override val optionCause: Option[Throwable],
                           override val isEnableSuppression: Boolean,
                           override val isWritableStackTrace: Boolean
                          ) extends GangException(optionMessage, optionCause, isEnableSuppression, isWritableStackTrace)


/**
 * KeyNotFoundException who extends RuntimeException
 */
object KeyNotFoundException {
  /**
   * 生成一个新的 KeyNotFoundException
   *
   * @return 一个新的 KeyNotFoundException
   */
  def apply: KeyNotFoundException = KeyNotFoundException()

  /**
   * 使用错误信息生成一个新的 KeyNotFoundException
   *
   * @param message 错误信息
   * @return 一个新的 KeyNotFoundException
   */
  def apply(message: String): KeyNotFoundException = KeyNotFoundException(optionMessage = Some(message))

  /**
   * 使用异常生成一个新的 KeyNotFoundException
   *
   * @param cause 异常
   * @return 一个新的 KeyNotFoundException
   */
  def apply(cause: Throwable): KeyNotFoundException = KeyNotFoundException(optionCause = Some(cause))

  /**
   * 使用错误信息、异常、isEnableSuppression、isWritableStackTrace 生成一个新的 KeyNotFoundException
   *
   * @param optionMessage        Option[错误信息]
   * @param optionCause          Option[异常]
   * @param isEnableSuppression  whether or not suppression is enabled or disabled
   * @param isWritableStackTrace whether or not the stack trace should be writable
   * @return 一个新的 KeyNotFoundException
   */
  def apply(optionMessage: Option[String] = None,
            optionCause: Option[Throwable] = None,
            isEnableSuppression: Boolean = false,
            isWritableStackTrace: Boolean = false): KeyNotFoundException =
    new KeyNotFoundException(optionMessage, optionCause, isEnableSuppression, isWritableStackTrace)

  /**
   * 使用错误信息和异常生成一个新的 KeyNotFoundException
   *
   * @param message 错误信息
   * @param cause   异常
   * @return 一个新的 KeyNotFoundException
   */
  def apply(message: String, cause: Throwable): KeyNotFoundException = KeyNotFoundException(optionMessage = Some(message), optionCause = Some(cause))
}