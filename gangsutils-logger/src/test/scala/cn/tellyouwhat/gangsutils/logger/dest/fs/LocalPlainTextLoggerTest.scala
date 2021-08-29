package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.constants.infoHead_unquote
import cn.tellyouwhat.gangsutils.logger.Logger
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import org.scalactic.TimesOnInt.convertIntToRepeater
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.{Files, Paths}
import scala.io.Source

class LocalPlainTextLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {
  val path = "log_text/1.txt"

  before {
    if (Files.exists(Paths.get(path))) {
      Files.delete(Paths.get(path))
      Files.delete(Paths.get(path).getParent)
    }
  }

  after {
    Files.delete(Paths.get(path))
    Files.delete(Paths.get(path).getParent)
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
    val source = Source.fromFile(path, "UTF-8")
    source.mkString should fullyMatch regex (infoHead_unquote + ": hello fs\\s+") * 20
    source.close()
  }

}
