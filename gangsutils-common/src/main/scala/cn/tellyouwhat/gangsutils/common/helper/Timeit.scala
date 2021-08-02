package cn.tellyouwhat.gangsutils.common.helper

import cn.tellyouwhat.gangsutils.common.gangfunctions.timeit
import cn.tellyouwhat.gangsutils.common.logger.GangLogger

trait Timeit {
  def run(desc: String)(implicit logger: GangLogger): Unit
}

trait TimeitLogger extends Timeit {
  abstract override def run(desc: String)(implicit logger: GangLogger): Unit = timeit(super.run(desc))(desc)
}

