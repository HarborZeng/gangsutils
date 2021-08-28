package cn.tellyouwhat.gangsutils.logger.exceptions

import cn.tellyouwhat.gangsutils.core.exceptions.GangException

class DiskSpaceLowException(override val optionMessage: Option[String],
                            override val optionCause: Option[Throwable],
                            override val isEnableSuppression: Boolean,
                            override val isWritableStackTrace: Boolean
                            ) extends GangException(optionMessage, optionCause, isEnableSuppression, isWritableStackTrace)


/**
 * DiskSpaceLowException who extends RuntimeException
 */
object DiskSpaceLowException {
  /**
   * 生成一个新的 DiskSpaceLowException
   *
   * @return 一个新的 DiskSpaceLowException
   */
  def apply: DiskSpaceLowException = DiskSpaceLowException()

  /**
   * 使用错误信息生成一个新的 DiskSpaceLowException
   *
   * @param message 错误信息
   * @return 一个新的 DiskSpaceLowException
   */
  def apply(message: String): DiskSpaceLowException = DiskSpaceLowException(optionMessage = Some(message))

  /**
   * 使用错误信息、异常、isEnableSuppression、isWritableStackTrace 生成一个新的 DiskSpaceLowException
   *
   * @param optionMessage        Option[错误信息]
   * @param optionCause          Option[异常]
   * @param isEnableSuppression  whether or not suppression is enabled or disabled
   * @param isWritableStackTrace whether or not the stack trace should be writable
   * @return 一个新的 DiskSpaceLowException
   */
  def apply(optionMessage: Option[String] = None,
            optionCause: Option[Throwable] = None,
            isEnableSuppression: Boolean = false,
            isWritableStackTrace: Boolean = false): DiskSpaceLowException =
    new DiskSpaceLowException(optionMessage, optionCause, isEnableSuppression, isWritableStackTrace)

  /**
   * 使用异常生成一个新的 DiskSpaceLowException
   *
   * @param cause 异常
   * @return 一个新的 DiskSpaceLowException
   */
  def apply(cause: Throwable): DiskSpaceLowException = DiskSpaceLowException(optionCause = Some(cause))

  /**
   * 使用错误信息和异常生成一个新的 DiskSpaceLowException
   *
   * @param message 错误信息
   * @param cause   异常
   * @return 一个新的 DiskSpaceLowException
   */
  def apply(message: String, cause: Throwable): DiskSpaceLowException = DiskSpaceLowException(optionMessage = Some(message), optionCause = Some(cause))
}