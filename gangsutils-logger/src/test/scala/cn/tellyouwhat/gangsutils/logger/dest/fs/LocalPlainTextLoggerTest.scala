package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.constants.infoHead_unquote
import cn.tellyouwhat.gangsutils.logger.{GangLogger, SupportedLogDest}
import org.scalactic.TimesOnInt.convertIntToRepeater
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.{Files, Paths}
import scala.io.Source

class LocalPlainTextLoggerTest extends AnyFlatSpec with Matchers {

  behavior of "LocalPlainTextLoggerTest"

  "setLogSavePath" should "set log save path" in {
    val path = "path/to/log.txt"
    LocalPlainTextLogger.setLogSavePath(path)
    GangLogger().logSavePath shouldBe Paths.get(path)
  }

  "LocalPlainTextLogger" should "log to local file with plain text" in {
    val path = "loglog/1.txt"
    LocalPlainTextLogger.setLogSavePath(path)
    GangLogger.setDefaultLogDest(SupportedLogDest.LOCAL_PLAIN_TEXT_LOGGER :: Nil)
    GangLogger.enableTrace()
    GangLogger.enableDateTime()
    GangLogger.enableHostname()
    val logger = GangLogger()
    200000 times logger.info("hello fs")
//    val source = Source.fromFile(path, "UTF-8")
//    source.mkString should fullyMatch regex (infoHead_unquote + ": hello fs\\s+") * 20
//    source.close()
//    Files.delete(Paths.get(path))
//    Files.delete(Paths.get(path).getParent)
  }

}
