package cn.tellyouwhat.gangsutils.logger.exceptions

import java.nio.file.FileSystemException

class NotFileException(
                        val optionFile: Option[String],
                        val optionOther: Option[String],
                        val optionReason: Option[String],
                      ) extends FileSystemException(
  optionFile match {
    case Some(file) => file
    case None => null
  },
  optionOther match {
    case Some(other) => other
    case None => null
  },
  optionReason match {
    case Some(reason) => reason
    case None => null
  }
)


/**
 * NotFileException who extends FileSystemException
 */
object NotFileException {
  /**
   * 生成一个新的 NotFileException
   *
   * @return 一个新的 NotFileException
   */
  def apply: NotFileException = NotFileException()


  def apply(file: String): NotFileException = NotFileException(optionFile = Some(file))

  def apply(optionFile: Option[String] = None,
            optionOther: Option[String] = None,
            optionReason: Option[String] = None): NotFileException =
    new NotFileException(optionFile, optionOther, optionReason)

  def apply(file: String, reason: String): NotFileException = NotFileException(optionFile = Some(file), optionReason = Some(reason))

  def apply(file: String, other: String, reason: String): NotFileException = NotFileException(optionFile = Some(file), optionOther = Some(other), optionReason = Some(reason))

}