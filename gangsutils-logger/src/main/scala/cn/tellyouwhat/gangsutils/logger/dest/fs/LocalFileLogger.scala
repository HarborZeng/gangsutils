package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.helper.ConfigReader
import cn.tellyouwhat.gangsutils.core.helper.chaining.TapIt
import cn.tellyouwhat.gangsutils.logger.exceptions.{DiskSpaceLowException, KeyNotFoundException, NotFileException}
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger}

import java.io.OutputStream
import java.nio.file._
import java.util.Objects

/**
 * Trait of LocalFileLogger for all locally-saved-as-files loggers, including open, close output stream and write
 * (checking file size to determine whether to move the log file with a timestamp-tailing new name)
 */
trait LocalFileLogger extends Logger with FileLifeCycle {

  /**
   * the file path to save log
   */
  private[fs] val logSavePath: Path = null

  /**
   * a lazy value of file name to save, if the underlying logSavePath is null, an IllegalStateException will be thrown
   */
  private lazy val logSaveFileName: Path = logSavePath match {
    case null => throw new IllegalStateException("The underlying logSavePath is null")
    case path => path.getFileName
  }

  /**
   * a lazy value of directory to save, if the underlying logSavePath is null, an IllegalStateException will be thrown,
   * if the path does not have parent, an IllegalStateException will be thrown
   */
  private lazy val logSaveDir: Path = logSavePath match {
    case null => throw new IllegalStateException("The underlying logSavePath is null")
    case path => path.getParent match {
      case null => throw new IllegalStateException(s"The underlying logSavePath: $logSavePath might does not have parent")
      case path => path
    }
  }

  /**
   * the optional of output stream for writing logs
   */
  private var optionOS: Option[OutputStream] = None

  /**
   * close the underlying output stream if optionOS is not None
   */
  def closeOutputStream(): Unit = optionOS match {
    case Some(os) => onEOF(os); os.close()
    case None =>
  }

  override protected def checkPrerequisite(): Unit = {
    super.checkPrerequisite()
    // make sure the logSavePath is not null
    Objects.requireNonNull(logSavePath)

    //target path can not be a directory
    if (Files.exists(logSavePath) && Files.isDirectory(logSavePath))
      throw NotFileException(logSavePath.toString, "Path is a directory, use specific file path instead")

    // create directory
    Files.createDirectories(logSaveDir)
    // usable space can not be less than 64M and must have write permission (getUsableSpace)
    val usableSpaceInMegabyte = logSaveDir.toFile.getUsableSpace / 1024 / 1024
    if (usableSpaceInMegabyte <= 64) {
      Files.delete(logSaveDir)
      throw DiskSpaceLowException(logSavePath.toString, s"Usable space only ${usableSpaceInMegabyte}M")
    }
  }

  /**
   * write a string to the file
   *
   * @param s string
   * @return always true unless an exception was thrown
   */
  protected def writeString(s: String): Boolean = writeBytes(s.getBytes("UTF-8"))

  /**
   * write byte array to the file
   *
   * @param logBytes the byte array which is encoded using UTF-8
   * @return always true unless an exception was thrown
   */
  protected def writeBytes(logBytes: Array[Byte]): Boolean = synchronized {
    getOS.write(logBytes)
    //append a new line
    getOS.write("\n".getBytes("UTF-8"))
    getOS.flush()

    val split = ConfigReader.getGangYamlConfig.hcursor
      .downField("logger")
      .downField("fs")
      .downField("localFile")
      .downField("split")
      .as[Boolean] match {
      case Left(e) => throw KeyNotFoundException(s"key logger.fs.localFile.split not found, e: $e")
      case Right(split) => split
    }

    if (split && isLogFileSizeTooLarge) {
      onEOF(getOS)
      // close os, ready to move
      getOS.close()
      val newFileName = logSaveFileName + s".${System.currentTimeMillis()}"
      val newSavePath = logSavePath.resolveSibling(newFileName)
      Files.move(logSavePath, newSavePath, StandardCopyOption.ATOMIC_MOVE)
      // reset optionOS to None
      optionOS = None
    }
    true
  }

  /**
   * get output stream from the underlying variable optionOS or create a new one,
   * if the file exists, append, if doesn't, create and hook on start of file.
   *
   * @return the output stream
   */
  private def getOS: OutputStream = optionOS match {
    case Some(os) => os
    case None =>
      val needHookSOF = !Files.exists(logSavePath)
      Files.newOutputStream(logSavePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)
        .tap(os => optionOS = Some(os))
        .tap(os => if (needHookSOF) onSOF(os))
  }

  /**
   * test whether logSavePath is larger than the configured byte number.
   *
   * lock outputStream, to prevent outputStream from writing after logSavePath is moved(renamed)
   *
   * @return the configured byte number of `logger.fs.localFile.blockSize`
   */
  private def isLogFileSizeTooLarge: Boolean =
    optionOS.synchronized {
      logSavePath.toFile.length() >= (ConfigReader.getGangYamlConfig.hcursor
        .downField("logger")
        .downField("fs")
        .downField("localFile")
        .downField("blockSize")
        .as[Int] match {
        case Left(e) => throw KeyNotFoundException(s"key logger.fs.localFile.blockSize not found in $logSavePath, e: $e")
        case Right(blockSize) => blockSize
      })
    }

  /**
   * the action to perform logging to file
   *
   * @param msg             the log message
   * @param optionThrowable the exception
   * @param level           the log level
   * @return
   */
  protected def fileLog(msg: String, optionThrowable: Option[Throwable], level: LogLevel.Value): Boolean

  override protected def doTheLogAction(msg: String, optionThrowable: Option[Throwable], level: LogLevel.Value): Boolean = fileLog(msg, optionThrowable, level)
}
