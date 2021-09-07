package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.constants.infoHead_unquote
import cn.tellyouwhat.gangsutils.core.helper.chaining.TapIt
import cn.tellyouwhat.gangsutils.logger.Logger
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import org.scalactic.TimesOnInt.convertIntToRepeater
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.{Files, LinkOption, Path, Paths}
import scala.io.Source

class LocalPlainTextLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {
  val path = "log_text/1.txt"
  val thePath: Path = Paths.get(path)

  def deletePath(): Unit = {
    val parentDir = thePath.getParent
    if (Files.exists(parentDir)) {
      Files.list(parentDir)
        .filter(p => !Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS))
        .forEach(p => Files.delete(p))
      Files.delete(parentDir)
    }
  }

  before {
    deletePath()
  }

  after {
    LocalPlainTextLogger.resetLogSavePath()
    LocalPlainTextLogger.resetConfiguration()
    deletePath()
  }

  behavior of "LocalPlainTextLoggerTest"

  "setLogSavePath" should "set log save path" in {
    LocalPlainTextLogger.setLogSavePath(path)
    LocalPlainTextLogger.initializeConfiguration(LoggerConfiguration())
    val logger = LocalPlainTextLogger()
    logger.asInstanceOf[LocalPlainTextLogger].logSavePath shouldBe Paths.get(path)
  }

  "LocalPlainTextLogger" should "log to local file with plain text" in {
    val logger: Logger = LocalPlainTextLogger(LoggerConfiguration(isDTEnabled = false, isHostnameEnabled = false), path)
    20 times logger.info("hello fs")

    logger.asInstanceOf[LocalPlainTextLogger].closeOutputStream()

    val source = Source.fromFile(path, "UTF-8")
    source.mkString |! (_ => source.close()) should fullyMatch regex (infoHead_unquote + ": hello fs\\s+") * 20
  }

  "LocalPlainTextLogger" should "be newed with an IllegalArgumentException thrown if logSavePath was not set" in {
    the [IllegalArgumentException] thrownBy new LocalPlainTextLogger() should have message "LocalPlainTextLogger.logSavePath is None"
  }

  it should "be newed with an IllegalArgumentException thrown if loggerConfig was not set" in {
    LocalPlainTextLogger.setLogSavePath("abc")
    the [IllegalArgumentException] thrownBy new LocalPlainTextLogger() should have message "LocalPlainTextLogger.loggerConfig is None"
  }

  it should "be applied with an IllegalArgumentException thrown if initializeConfiguration(c: LoggerConfiguration) or apply(c: LoggerConfiguration) was not set" in {
    the [IllegalArgumentException] thrownBy LocalPlainTextLogger() should have message "You did not pass parameter loggerConfig nor initializeConfiguration"
  }
}
