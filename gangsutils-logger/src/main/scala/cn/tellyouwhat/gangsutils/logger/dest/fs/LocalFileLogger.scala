package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.helper.ConfigReader
import cn.tellyouwhat.gangsutils.core.helper.chaining.TapIt
import cn.tellyouwhat.gangsutils.logger.exceptions.{DiskSpaceLowException, KeyNotFoundException, NotFileException}
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger}

import java.io.OutputStream
import java.nio.file._

trait LocalFileLogger extends Logger with FileLifeCycle {

  private[fs] val logSavePath: Path = null
  private lazy val logSaveFileName: Path = logSavePath match {
    case null => throw new IllegalStateException("The underlying logSavePath is null")
    case path => path.getFileName
  }
  private lazy val logSaveDir: Path = logSavePath match {
    case null => throw new IllegalStateException("The underlying logSavePath is null")
    case path => path.getParent
  }
  private var optionOS: Option[OutputStream] = None

  def closeOutputStream(): Unit = optionOS match {
    case Some(os) => os.close()
    case None =>
  }

  override protected def checkPrerequisite(): Unit = {
    super.checkPrerequisite()
    if (logSavePath == null)
      throw new IllegalStateException("The underlying logSavePath is null")
    //target path can not be a directory
    if (Files.exists(logSavePath) && Files.isDirectory(logSavePath))
      throw NotFileException(logSavePath.toString, "Path is a directory, use specific file path instead")

    // create directory
    if (logSaveDir == null)
      throw new IllegalStateException(s"The underlying logSaveDir is null, logSavePath: $logSavePath might does not have parent")
    Files.createDirectories(logSaveDir)
    // usable space can not be less than 64M and must have write permission (getUsableSpace)
    val usableSpaceInMegabyte = logSaveDir.toFile.getUsableSpace / 1024 / 1024
    if (usableSpaceInMegabyte <= 64) {
      Files.delete(logSaveDir)
      throw DiskSpaceLowException(logSavePath.toString, s"Usable space only ${usableSpaceInMegabyte}M")
    }
  }

  protected def writeString(s: String): Boolean = writeBytes(s.getBytes("UTF-8"))

  protected def writeBytes(logBytes: Array[Byte]): Boolean = {
    getOS.write(logBytes)
    getOS.write("\n".getBytes("UTF-8"))
    getOS.flush()

    if (isLogFileSizeTooLarge) {
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

  private def getOS: OutputStream = optionOS match {
    case Some(os) => os
    case None =>
      val needHookSOF = !Files.exists(logSavePath)
      Files.newOutputStream(logSavePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)
        .tap(os => optionOS = Some(os))
        .tap(os => if (needHookSOF) onSOF(os))
  }

  /**
   * test whether logSavePath is larger than byte number
   * lock outputStream, to prevent outputStream write after logSavePath moved
   *
   * @return
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

  protected def fileLog(msg: String, level: LogLevel.Value): Boolean

  override protected def doTheLogAction(msg: String, level: LogLevel.Value): Boolean = fileLog(msg, level)
}
