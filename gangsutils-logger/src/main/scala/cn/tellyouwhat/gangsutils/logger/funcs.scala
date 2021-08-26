package cn.tellyouwhat.gangsutils.logger

import cn.tellyouwhat.gangsutils.core.constants.placeholderHead_unquote
import cn.tellyouwhat.gangsutils.core.funcs.calcExecDuration
import cn.tellyouwhat.gangsutils.core.helper.I18N

import scala.util.{Failure, Success, Try}

object funcs {

  /**
   * 计时 + 切面日志
   *
   * @param block 要执行的方法
   * @param desc  描述，将作用于切面日志
   * @tparam R 返回值 Type
   * @return block 的执行结果
   */
  def timeit[R](block: => R, desc: String = I18N.getRB.getString("task")): R = {
    implicit val logger: Logger = GangLogger.getLogger
    printOrLog(I18N.getRB.getString("timeit.start").format(desc))
    val t0 = System.currentTimeMillis()
    val result = Try(block) match {
      case Failure(e) =>
        val t1 = System.currentTimeMillis()
        printOrLog(I18N.getRB.getString("timeit.failed").format(desc, calcExecDuration(t0, t1)), level = LogLevel.CRITICAL)
        throw e
      case Success(v) => v
    }
    val t1 = System.currentTimeMillis()
    printOrLog(I18N.getRB.getString("timeit.finished").format(desc, calcExecDuration(t0, t1)), level = LogLevel.SUCCESS)
    result
  }

  /**
   * print 或者执行 logger
   *
   * @param content 要输出的内容
   * @param level   日志级别
   * @param logger  日志对象
   */
  def printOrLog(content: String, level: LogLevel.Value = LogLevel.TRACE)(implicit logger: Logger = null): Unit =
    if (logger == null) {
      println(s"${placeholderHead_unquote.format(level)}: $content")
    } else {
      logger.log(content, level)
    }
}
