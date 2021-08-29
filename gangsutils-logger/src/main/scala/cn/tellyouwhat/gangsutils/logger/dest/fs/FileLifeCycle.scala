package cn.tellyouwhat.gangsutils.logger.dest.fs

import java.io.OutputStream

trait FileLifeCycle {
  /**
   * hook end of file. you can do something when file ends, like appending some text
   *
   * @param os output stream
   */
  def onEOF(os: OutputStream): Unit

  /**
   * hook start of file. you can do something when file start, like appending some text
   *
   * @param os output stream
   */
  def onSOF(os: OutputStream): Unit
}
