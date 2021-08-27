package cn.tellyouwhat.gangsutils.core.cc

import cn.tellyouwhat.gangsutils.core.funcs
import cn.tellyouwhat.gangsutils.core.helper.chaining.PipeIt
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


case class Person(name: String, age: Int) extends Mappable

class MappableTest extends AnyFlatSpec with Matchers {


  "Person(\"harbor\", 10)" should "can be converted to an equivalent map" in {
    val harborCC = Person("harbor", 10)
    val harborMap = harborCC |> funcs.ccToMap
    harborMap should have size 2
    harborMap("name") shouldEqual harborCC.name
    harborMap("age") shouldEqual harborCC.age
  }

}
