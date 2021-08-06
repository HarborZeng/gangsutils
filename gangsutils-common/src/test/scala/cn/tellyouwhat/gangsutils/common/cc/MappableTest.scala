package cn.tellyouwhat.gangsutils.common.cc

import cn.tellyouwhat.gangsutils.common.gangfunctions.ccToMap
import cn.tellyouwhat.gangsutils.common.helper.chaining.{PipeIt, TapIt}
import org.scalatest.funsuite.AnyFunSuite


class MappableTest extends AnyFunSuite  {

  case class Person(name: String, age: Int) extends Mappable

  test("Person(\"harbor\", 10) should can be converted to map") {
    assert(Person("harbor", 10) |> ccToMap |> (m => m("name") == "harbor" && m("age") == 10))
  }

}
