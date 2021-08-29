package cn.tellyouwhat.gangsutils.logger.exceptions

import java.nio.file.FileSystemException

class DiskSpaceLowException(
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
 * DiskSpaceLowException who extends FileSystemException
 */
object DiskSpaceLowException {
  /**
   * 生成一个新的 DiskSpaceLowException
   *
   * @return 一个新的 DiskSpaceLowException
   */
  def apply: DiskSpaceLowException = DiskSpaceLowException()


  def apply(file: String): DiskSpaceLowException = DiskSpaceLowException(optionFile = Some(file))

  def apply(file: String, reason: String): DiskSpaceLowException = DiskSpaceLowException(optionFile = Some(file), optionReason = Some(reason))

  def apply(file: String, other: String, reason: String): DiskSpaceLowException = DiskSpaceLowException(optionFile = Some(file), optionOther = Some(other), optionReason = Some(reason))


  def apply(optionFile: Option[String] = None,
            optionOther: Option[String] = None,
            optionReason: Option[String] = None): DiskSpaceLowException =
    new DiskSpaceLowException(optionFile, optionOther, optionReason)

}