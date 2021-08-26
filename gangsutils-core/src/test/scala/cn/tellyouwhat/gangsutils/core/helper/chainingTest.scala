package cn.tellyouwhat.gangsutils.core.helper

import cn.tellyouwhat.gangsutils.core.helper.chaining.{PipeIt, TapIt}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.Properties

class chainingTest extends AnyFlatSpec with Matchers {

  def plus1(i: Int): Int = i + 1

  def double(i: Int): Int = i * 2

  def square(i: Int): Int = i * i

  "|>" should "apply a function to the value and return the function result" in {
    // ((3 + 1) * 2 + 1)^2 = 81
    3 |> plus1 |> double |> plus1 |> square shouldEqual 81
  }

  "pipe" should "apply a function to the value and return the function result" in {
    3 pipe plus1 pipe double pipe plus1 pipe square shouldEqual 81
    3.pipe(plus1).pipe(double).pipe(plus1).pipe(square) shouldEqual 81
  }

  "|!" should "apply a function on the value but return the value itself" in {
    val properties = new Properties() |! (_.put("username", "harbor")) |! (_.put("password", "123"))

    properties should have size 2
    properties.get("username") shouldEqual "harbor"
    properties.get("password") shouldEqual "123"
  }

  "tap" should "apply a function on the value but return the value itself" in {
    val properties = new Properties()
      .tap(_.put("username", "harbor"))
      .tap(_.put("password", "123"))

    properties should have size 2
    properties.get("username") shouldEqual "harbor"
    properties.get("password") shouldEqual "123"
  }

}
