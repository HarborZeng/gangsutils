package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.helper.chaining.TapIt
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import org.scalactic.TimesOnInt.convertIntToRepeater
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.{Files, LinkOption, Path, Paths}
import scala.io.Source

class LocalHtmlLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {
  val path = "log_html/1.html"
  val thePath: Path = Paths.get(path)

  val parentDir: Path = thePath.getParent

  def deletePath(): Unit = {
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
    LocalHtmlLogger.resetLogSavePath()
    LocalHtmlLogger.resetConfiguration()
    deletePath()
  }

  "setLogSavePath" should "set log save path" in {
    LocalHtmlLogger.setLogSavePath(path)
    LocalHtmlLogger.initializeConfiguration(LoggerConfiguration())
    val logger = LocalHtmlLogger()
    logger.asInstanceOf[LocalHtmlLogger].logSavePath shouldBe Paths.get(path)
  }

  "LocalHtmlLogger" should "log to local file with colorful html" in {
    val logger = LocalHtmlLogger(LoggerConfiguration(isDTEnabled = false, isHostnameEnabled = false), path)

    2 times logger.info("hello html info")
    2 times logger.success("hello html success")
    2 times logger.error("hello html error")
    2 times logger.warning("hello html warning")
    2 times logger.critical("hello html critical")

    logger.asInstanceOf[LocalHtmlLogger].closeOutputStream()

    val source = Source.fromFile(path, "UTF-8")
    // simple test, I don't want to get deep in html regexp match
    source.mkString |! (_ => source.close()) should (startWith("<!DOCTYPE html>") and endWith(s"""</body></html>"""))
  }

  it should "log to local file with colorful html and rename the log file to timestamp-tailing name and continue logging with the old name" in {
    val logger = LocalHtmlLogger(LoggerConfiguration(isTraceEnabled = true, logPrefix = Some("some prefix string")), path)

    2000 times logger.info("hello html info")
    2000 times logger.success("hello html success")
    1000 times logger.error("hello html error")
    1000 times logger.warning("hello html warning")
    1000 times logger.critical("hello html critical")

    logger.asInstanceOf[LocalHtmlLogger].closeOutputStream()

    Files.list(parentDir).toArray should have length 2
  }

  // it will success in shell, but won't in Intellij Idea
  /*  it should "a NotFileException should be thrown if logSavePath was a directory" in {
      val logger = LocalHtmlLogger(LoggerConfiguration(), "gangsutils-logger")
      the [IllegalStateException] thrownBy logger.info("") should have message "The underlying logSavePath: gangsutils-logger might does not have parent"
    }*/

  it should "be newed with an IllegalArgumentException thrown if logSavePath was not set" in {
    the[IllegalArgumentException] thrownBy new LocalHtmlLogger() should have message "LocalHtmlLogger.logSavePath is None"
  }

  it should "be newed with an IllegalArgumentException thrown if loggerConfig was not set" in {
    LocalHtmlLogger.setLogSavePath("abc")
    the[IllegalArgumentException] thrownBy new LocalHtmlLogger() should have message "LocalHtmlLogger.loggerConfig is None"
  }

  it should "be applied with an IllegalArgumentException thrown if initializeConfiguration(c: LoggerConfiguration) or apply(c: LoggerConfiguration) was not set" in {
    the[IllegalArgumentException] thrownBy LocalHtmlLogger() should have message "You did not pass parameter loggerConfig nor initializeConfiguration"
  }
}
