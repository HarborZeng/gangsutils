package cn.tellyouwhat.gangsutils.logger.dest.fs

import cn.tellyouwhat.gangsutils.core.constants.criticalHead_unquote
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import org.scalactic.TimesOnInt.convertIntToRepeater
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.{Files, Path, Paths}
import scala.io.Source

class LocalHtmlLoggerTest extends AnyFlatSpec with Matchers with BeforeAndAfter {
  val path = "log_html/1.html"
  val thePath: Path = Paths.get(path)

  before {
    if (Files.exists(thePath)) {
      Files.delete(thePath)
      Files.delete(thePath.getParent)
    }
  }

  after {
    if (Files.exists(thePath)) {
      Files.delete(thePath)
      Files.delete(thePath.getParent)
    }
  }

  "setLogSavePath" should "set log save path" in {
    LocalPlainTextLogger.setLogSavePath(path)
    LocalPlainTextLogger.initializeConfiguration(LoggerConfiguration())
    val logger = LocalPlainTextLogger()
    logger.asInstanceOf[LocalPlainTextLogger].logSavePath shouldBe Paths.get(path)
  }

  "LocalPlainTextLogger" should "log to local file with plain text" in {
    val logger = LocalHtmlLogger(LoggerConfiguration(isDTEnabled = false, isHostnameEnabled = false), path)

    2 times logger.info("hello html info")
    2 times logger.success("hello html success")
    2 times logger.error("hello html error")
    2 times logger.warning("hello html warning")
    2 times logger.critical("hello html critical")

    val source = Source.fromFile(path, "UTF-8")
    // simple test, I don't want to get deep in html regexp match
    source.mkString should (startWith("<!DOCTYPE html>") and endWith(s"""<div class="log"><div class="head critical">$criticalHead_unquote</div><pre>: hello html critical</pre></div>\n"""))
    source.close()
  }

}
