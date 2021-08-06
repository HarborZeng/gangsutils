package cn.tellyouwhat.gangsutils.common.helper

/**
 * chaining implicit helpers for scala 2.12.x
 * <pre>
 * import cn.tellyouwhat.gangsutils.common.helper.chaining._
 * </pre>
 *
 * @see <a>https://alvinalexander.com/scala/scala-2.13-pipe-tap-chaining-operations/</a> for more details
 */
object chaining {
  implicit class TapIt[A](a: A) {
    /**
     * Similarly, tap reminds me of the Unix tee command. tap does two things:
     *
     * <li>Returns self</li>
     * <li>Lets you perform a side effect with the value, such as printing/logging it</li>
     *
     * <pre>
     * import cn.tellyouwhat.gangsutils.common.helper.chaining._
     * calcSomeDF(spark)
     * .tap(_.printSchema())
     * .tap(_.cache().show())
     * .write.mode("overwrite").parquet(savepath)
     * </pre>
     *
     * @param fun the function to apply
     * @return self
     */
    def tap(fun: A => Unit): A = {
      fun(a)
      a
    }

    /**
     * Similarly, tap reminds me of the Unix tee command. tap does two things:
     *
     * <li>Returns self</li>
     * <li>Lets you perform a side effect with the value, such as printing/logging it</li>
     *
     * <pre>
     * import cn.tellyouwhat.gangsutils.common.helper.chaining._
     * calcSomeDF(spark) |! (_.printSchema()) |! (_.cache().show())
     * </pre>
     *
     * @param fun the function to apply
     * @return self
     */
    def |!(fun: A => Unit): A = tap(fun)
  }

  implicit class PipeIt[A](a: A) {
    /**
     * pipe works like a Unix pipe
     *
     * <pre>
     * import cn.tellyouwhat.gangsutils.common.helper.chaining._
     * calcSomeDF(spark)
     * .pipe(model.transform)
     * .write.mode("overwrite").parquet(savepath)
     * </pre>
     *
     * @param fun the function to apply
     * @tparam T return type
     * @return function invocation result
     */
    def pipe[T](fun: A => T): T = fun(a)

    /**
     * pipe works like a Unix pipe
     *
     * <pre>
     * import cn.tellyouwhat.gangsutils.common.helper.chaining._
     * val transformedDF = someDataframe |> model.transform
     * </pre>
     *
     * @param fun the function to apply
     * @tparam T return type
     * @return function invocation result
     */
    def |>[T](fun: A => T): T = pipe[T](fun)
  }
}
