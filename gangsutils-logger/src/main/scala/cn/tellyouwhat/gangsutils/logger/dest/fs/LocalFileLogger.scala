package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.exceptions.GangException
import cn.tellyouwhat.gangsutils.logger.{LogLevel, Logger}

import java.io.OutputStream
import java.nio.file.{Files, Path, Paths, StandardOpenOption}

trait LocalFileLogger extends Logger {

  val logSavePath: Path = Paths.get(LocalFileLogger.logSavePath)
  lazy val logSaveFileName: Path = logSavePath.getFileName
  lazy val logSaveDir: Path = logSavePath.getParent
  lazy val os: OutputStream = Files.newOutputStream(logSavePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)

  override protected def checkPrerequisite(): Unit = {
    if (Files.exists(logSavePath) && Files.isDirectory(logSavePath))
      throw GangException(s"$logSavePath can't be a directory, use specific file path")
    Files.createDirectories(logSaveDir)
  }

  protected def writeString(s: String): Boolean = writeBytes(s.getBytes("UTF-8"))

  protected def writeBytes(logBytes: Array[Byte]): Boolean = {
    os.write(logBytes)
    os.write("\n".getBytes("UTF-8"))
    os.flush()
    true
  }

  protected def fileLog(msg: String, level: LogLevel.Value): Boolean

}

object LocalFileLogger {
  private var logSavePath: String = _

  def setLogSavePath(path: String): Unit = logSavePath = path
}