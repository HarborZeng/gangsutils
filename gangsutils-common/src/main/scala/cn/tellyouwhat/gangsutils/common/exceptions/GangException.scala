package cn.tellyouwhat.gangsutils.common.exceptions

object GangException {
  def apply: GangException = GangException()

  def apply(message: String): GangException = GangException(optionMessage = Some(message))

  def apply(cause: Throwable): GangException = GangException(optionCause = Some(cause))

  def apply(message: String, cause: Throwable): GangException = GangException(optionMessage = Some(message), optionCause = Some(cause))

  def apply(optionMessage: Option[String] = None,
            optionCause: Option[Throwable] = None,
            isEnableSuppression: Boolean = false,
            isWritableStackTrace: Boolean = false): GangException =
    new GangException(optionMessage, optionCause, isEnableSuppression, isWritableStackTrace)
}

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