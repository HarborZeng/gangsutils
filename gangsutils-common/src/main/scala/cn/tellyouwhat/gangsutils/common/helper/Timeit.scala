package cn.tellyouwhat.gangsutils.common.helper

import cn.tellyouwhat.gangsutils.common.gangfunctions.timeit
import cn.tellyouwhat.gangsutils.common.logger.BaseLogger

/**
 * 计时日志特质
 */
trait Timeit {
  /**
   * 实现此方法，即可获得日志功能和计时功能
   *
   * @param desc   对此次计时任务的描述
   */
  def run(desc: String = "任务"): Unit
}

/**
 * 计时日志切面
 */
trait TimeitLogger extends Timeit {
  /**
   * @inheritdoc
   * @param desc   对此次计时任务的描述
   */
  abstract override def run(desc: String): Unit = timeit(super.run(desc), desc)
}

