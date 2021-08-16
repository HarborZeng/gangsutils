package cn.tellyouwhat.gangsutils.common.exceptions

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WrongLogLevelExceptionTest extends AnyFlatSpec with Matchers  {
  "WrongLogLevelException.apply" should "return a new empty WrongLogLevelException" in {
    val e1 = WrongLogLevelException.apply
    val e2 = new WrongLogLevelException(None, None, false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "WrongLogLevelException.apply(message: String)" should "return a new WrongLogLevelException with message" in {
    val message = "some exceptions"
    val e1 = WrongLogLevelException.apply(message)
    val e2 = new WrongLogLevelException(Some(message), None, false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "WrongLogLevelException.apply(cause: Throwable) " should "return a new WrongLogLevelException with cause" in {
    val cause = WrongLogLevelException("I am the cause")
    val e1 = WrongLogLevelException.apply(cause)
    val e2 = new WrongLogLevelException(None, Some(cause), false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "WrongLogLevelException.apply(message: String, cause: Throwable)" should "return a new WrongLogLevelException with message and cause" in {
    val message = "some exceptions"
    val cause = WrongLogLevelException("I am the cause")
    val e1 = WrongLogLevelException.apply(message, cause)
    val e2 = new WrongLogLevelException(Some(message), Some(cause), false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "WrongLogLevelException.apply(message: String, cause: Throwable)" should "return a new WrongLogLevelException with null message and null cause" in {
    val message = None
    val cause = None
    val e1 = WrongLogLevelException.apply(message, cause)

    e1.optionMessage shouldBe None
    e1.optionCause shouldBe None
  }

  "WrongLogLevelException.apply(optionMessage: Option[String] = None, optionCause: Option[Throwable] = None, isEnableSuppression: Boolean = false, isWritableStackTrace: Boolean = false)" should "return a new WrongLogLevelException with message, cause, isEnableSuppression and isWritableStackTrace" in {
    val message = "some exceptions"
    val cause = WrongLogLevelException("I am the cause")
    val e1 = WrongLogLevelException.apply(Some(message), Some(cause), isEnableSuppression = true, isWritableStackTrace = true)
    val e2 = new WrongLogLevelException(Some(message), Some(cause), true, true)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

}
