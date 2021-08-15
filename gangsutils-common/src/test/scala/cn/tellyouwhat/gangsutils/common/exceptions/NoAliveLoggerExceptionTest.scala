package cn.tellyouwhat.gangsutils.common.exceptions

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NoAliveLoggerExceptionTest extends AnyFlatSpec with Matchers  {
  "NoAliveLoggerException.apply" should "return a new empty NoAliveLoggerException" in {
    val e1 = NoAliveLoggerException.apply
    val e2 = new NoAliveLoggerException(None, None, false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "NoAliveLoggerException.apply(message: String)" should "return a new NoAliveLoggerException with message" in {
    val message = "some exceptions"
    val e1 = NoAliveLoggerException.apply(message)
    val e2 = new NoAliveLoggerException(Some(message), None, false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "NoAliveLoggerException.apply(cause: Throwable) " should "return a new NoAliveLoggerException with cause" in {
    val cause = NoAliveLoggerException("I am the cause")
    val e1 = NoAliveLoggerException.apply(cause)
    val e2 = new NoAliveLoggerException(None, Some(cause), false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "NoAliveLoggerException.apply(message: String, cause: Throwable)" should "return a new NoAliveLoggerException with message and cause" in {
    val message = "some exceptions"
    val cause = NoAliveLoggerException("I am the cause")
    val e1 = NoAliveLoggerException.apply(message, cause)
    val e2 = new NoAliveLoggerException(Some(message), Some(cause), false, false)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

  "NoAliveLoggerException.apply(message: String, cause: Throwable)" should "return a new NoAliveLoggerException with null message and null cause" in {
    val message = None
    val cause = None
    val e1 = NoAliveLoggerException.apply(message, cause)

    e1.optionMessage shouldBe None
    e1.optionCause shouldBe None
  }

  "NoAliveLoggerException.apply(optionMessage: Option[String] = None, optionCause: Option[Throwable] = None, isEnableSuppression: Boolean = false, isWritableStackTrace: Boolean = false)" should "return a new NoAliveLoggerException with message, cause, isEnableSuppression and isWritableStackTrace" in {
    val message = "some exceptions"
    val cause = NoAliveLoggerException("I am the cause")
    val e1 = NoAliveLoggerException.apply(Some(message), Some(cause), isEnableSuppression = true, isWritableStackTrace = true)
    val e2 = new NoAliveLoggerException(Some(message), Some(cause), true, true)

    e1.optionMessage shouldEqual e2.optionMessage
    e1.optionCause shouldEqual e2.optionCause
    e1.isEnableSuppression shouldEqual e2.isEnableSuppression
    e1.isWritableStackTrace shouldEqual e2.isWritableStackTrace
  }

}
