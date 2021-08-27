package cn.tellyouwhat.gangsutils.core.cc

/**
 * 可转为 Map 的特质，凡是混入了此特质，
 * 即可调用 `cn.tellyouwhat.gangsutils.common.gangfunctions#ccToMap`
 * 将其转为 Map[String, Any]
 */
trait Mappable