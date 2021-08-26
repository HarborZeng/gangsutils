package cn.tellyouwhat.gangsutils.core.exceptions

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GangExceptionTest extends AnyFlatSpec with Matchers {
  "GangException.apply" should "return a new empty GangException" in {
    val e1 = GangException.apply
    val e2 = new GangException(None, None, false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "GangException.apply(message: String)" should "return a new GangException with message" in {
    val message = "some exceptions"
    val e1 = GangException.apply(message)
    val e2 = new GangException(Some(message), None, false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "GangException.apply(cause: Throwable) " should "return a new GangException with cause" in {
    val cause = GangException("I am the cause")
    val e1 = GangException.apply(cause)
    val e2 = new GangException(None, Some(cause), false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "GangException.apply(message: String, cause: Throwable)" should "return a new GangException with message and cause" in {
    val message = "some exceptions"
    val cause = GangException("I am the cause")
    val e1 = GangException.apply(message, cause)
    val e2 = new GangException(Some(message), Some(cause), false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "GangException.apply(message: String, cause: Throwable)" should "return a new GangException with null message and null cause" in {
    val message = None
    val cause = None
    val e1 = GangException.apply(message, cause)

    e1.optionMessage shouldBe None
    e1.optionCause shouldBe None
  }

  "GangException.apply(optionMessage: Option[String] = None, optionCause: Option[Throwable] = None, isEnableSuppression: Boolean = false, isWritableStackTrace: Boolean = false)" should "return a new GangException with message, cause, isEnableSuppression and isWritableStackTrace" in {
    val message = "some exceptions"
    val cause = GangException("I am the cause")
    val e1 = GangException.apply(Some(message), Some(cause), isEnableSuppression = true, isWritableStackTrace = true)
    val e2 = new GangException(Some(message), Some(cause), true, true)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

}
