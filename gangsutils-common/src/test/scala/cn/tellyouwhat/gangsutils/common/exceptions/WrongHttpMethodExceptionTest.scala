package cn.tellyouwhat.gangsutils.common.exceptions

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WrongHttpMethodExceptionTest extends AnyFlatSpec with Matchers  {
  "WrongHttpMethodException.apply" should "return a new empty WrongHttpMethodException" in {
    val e1 = WrongHttpMethodException.apply
    val e2 = new WrongHttpMethodException(None, None, false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "WrongHttpMethodException.apply(message: String)" should "return a new WrongHttpMethodException with message" in {
    val message = "some exceptions"
    val e1 = WrongHttpMethodException.apply(message)
    val e2 = new WrongHttpMethodException(Some(message), None, false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "WrongHttpMethodException.apply(cause: Throwable) " should "return a new WrongHttpMethodException with cause" in {
    val cause = WrongHttpMethodException("I am the cause")
    val e1 = WrongHttpMethodException.apply(cause)
    val e2 = new WrongHttpMethodException(None, Some(cause), false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "WrongHttpMethodException.apply(message: String, cause: Throwable)" should "return a new WrongHttpMethodException with message and cause" in {
    val message = "some exceptions"
    val cause = WrongHttpMethodException("I am the cause")
    val e1 = WrongHttpMethodException.apply(message, cause)
    val e2 = new WrongHttpMethodException(Some(message), Some(cause), false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "WrongHttpMethodException.apply(message: String, cause: Throwable)" should "return a new WrongHttpMethodException with null message and null cause" in {
    val message = None
    val cause = None
    val e1 = WrongHttpMethodException.apply(message, cause)

    e1.optionMessage shouldBe None
    e1.optionCause shouldBe None
  }

  "WrongHttpMethodException.apply(optionMessage: Option[String] = None, optionCause: Option[Throwable] = None, isEnableSuppression: Boolean = false, isWritableStackTrace: Boolean = false)" should "return a new WrongHttpMethodException with message, cause, isEnableSuppression and isWritableStackTrace" in {
    val message = "some exceptions"
    val cause = WrongHttpMethodException("I am the cause")
    val e1 = WrongHttpMethodException.apply(Some(message), Some(cause), isEnableSuppression = true, isWritableStackTrace = true)
    val e2 = new WrongHttpMethodException(Some(message), Some(cause), true, true)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

}
