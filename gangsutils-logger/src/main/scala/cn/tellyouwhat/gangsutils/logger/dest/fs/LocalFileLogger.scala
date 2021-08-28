package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.helper.ConfigReader
import cn.tellyouwhat.gangsutils.core.helper.chaining.TapIt
import cn.tellyouwhat.gangsutils.logger.exceptions.{DiskSpaceLowException, KeyNotFoundException, NotFileException}
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger}

import java.io.OutputStream
import java.nio.file._

trait LocalFileLogger extends Logger {

  val logSavePath: Path = LocalFileLogger.logSavePath match {
    case Some(path) => Paths.get(path)
    case None => null
  }
  lazy val logSaveFileName: Path = logSavePath.getFileName
  lazy val logSaveDir: Path = logSavePath.getParent
  var optionOS: Option[OutputStream] = None

  override protected def checkPrerequisite(): Unit = {
    //target path can not be a directory
    if (Files.exists(logSavePath) && Files.isDirectory(logSavePath))
      throw NotFileException(logSavePath.toString, "path is a directory, use specific file path instead")

    // create directory
    Files.createDirectories(logSaveDir)
    // usable space can not less than 64M and must have write permission (getUsableSpace)
    val usableSpaceInMegabyte = logSaveDir.toFile.getUsableSpace / 1024 / 1024
    if (usableSpaceInMegabyte <= 64) {
      Files.delete(logSaveDir)
      throw DiskSpaceLowException(s"Usable space on $logSavePath only ${usableSpaceInMegabyte}M")
    }
  }

  protected def writeString(s: String): Boolean = writeBytes(s.getBytes("UTF-8"))

  protected def getOS: OutputStream = optionOS match {
    case Some(os) => os
    case None =>
      Files.newOutputStream(logSavePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)
        .tap(os => optionOS = Some(os))
  }

  protected def writeBytes(logBytes: Array[Byte]): Boolean = {
    getOS.write(logBytes)
    getOS.write("\n".getBytes("UTF-8"))
    getOS.flush()

    if (isLogFileSizeTooLarge) {
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
   * test whether logSavePath is larger than byte number
   * lock outputStream, to prevent outputStream write after logSavePath removed
   *
   * @return
   */
  def isLogFileSizeTooLarge: Boolean =
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

}

object LocalFileLogger {

  /**
   * Log save path, which is a full path, default is None
   */
  private var logSavePath: Option[String] = None

  /**
   * default is None if you don't set
   *
   * @param path Log save path, which is a full path
   */
  def setLogSavePath(path: String): Unit = logSavePath = Some(path)

  def resetLogSavePath(): Unit = logSavePath = None
}