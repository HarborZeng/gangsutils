# gangsutils

| __Goal__                  | Badges                                                       |
| :------------------------ | :----------------------------------------------------------- |
| __Packages and Releases__ | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.tellyouwhat/gangsutils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.tellyouwhat/gangsutils) [![Sonatype Nexus](https://img.shields.io/nexus/r/cn.tellyouwhat/gangsutils?server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/releases/cn/tellyouwhat/gangsutils/) |
| __Java Documentation__    | [![javadoc](https://javadoc.io/badge2/cn.tellyouwhat/gangsutils-common/javadoc.svg)](https://javadoc.io/doc/cn.tellyouwhat/gangsutils-common) |
| __Build Status__          | [![develop build](https://github.com/HarborZeng/gangsutils/actions/workflows/build.yml/badge.svg?branch=develop)](https://github.com/HarborZeng/gangsutils/actions/workflows/build.yml) [![master build](https://github.com/HarborZeng/gangsutils/actions/workflows/master-build.yml/badge.svg?branch=master)](https://github.com/HarborZeng/gangsutils/actions/workflows/master-build.yml) |
| __JaCoCo Test Coverage__  | [![codecov](https://codecov.io/gh/HarborZeng/gangsutils/branch/master/graph/badge.svg?token=MUYXET5V4O)](https://codecov.io/gh/HarborZeng/gangsutils) |
| __License__               | [![License](https://img.shields.io/badge/License-Apache%20License%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) [![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FHarborZeng%2Fgangsutils.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FHarborZeng%2Fgangsutils?ref=badge_shield) |

## Utilities

To use this utils pack right away, add the following dependency to pom.xml or other dependencies management tools.

Replace `${gangsutils.version}` to the latest stable version showed in the Maven Central
badge [![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.tellyouwhat/gangsutils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.tellyouwhat/gangsutils)

**Maven**

```xml
<dependency>
    <groupId>cn.tellyouwhat</groupId>
    <artifactId>gangsutils-logger</artifactId>
    <version>${gangsutils.version}</version>
</dependency>
<dependency>
    <groupId>cn.tellyouwhat</groupId>
    <artifactId>gangsutils-spark</artifactId>
    <version>${gangsutils.version}</version>
</dependency>
<dependency>
    <groupId>cn.tellyouwhat</groupId>
    <artifactId>gangsutils-hadoop</artifactId>
    <version>${gangsutils.version}</version>
</dependency>
<dependency>
    <groupId>cn.tellyouwhat</groupId>
    <artifactId>gangsutils-core</artifactId>
    <version>${gangsutils.version}</version>
</dependency>
```

or import all by

```xml
<dependency>
    <groupId>cn.tellyouwhat</groupId>
    <artifactId>gangsutils-all</artifactId>
    <version>${gangsutils.version}</version>
</dependency>
```

**SBT**

```scala
libraryDependencies += "cn.tellyouwhat" % "gangsutils-logger" % "${gangsutils.version}"
libraryDependencies += "cn.tellyouwhat" % "gangsutils-spark" % "${gangsutils.version}"
libraryDependencies += "cn.tellyouwhat" % "gangsutils-hadoop" % "${gangsutils.version}"
libraryDependencies += "cn.tellyouwhat" % "gangsutils-core" % "${gangsutils.version}"
```

or import all by

```scala
libraryDependencies += "cn.tellyouwhat" % "gangsutils-all" % "${gangsutils.version}"
```

**Gradle**

```groovy
implementation 'cn.tellyouwhat:gangsutils-logger:${gangsutils.version}'
implementation 'cn.tellyouwhat:gangsutils-spark:${gangsutils.version}'
implementation 'cn.tellyouwhat:gangsutils-hadoop:${gangsutils.version}'
implementation 'cn.tellyouwhat:gangsutils-core:${gangsutils.version}'
```

or import all by

```groovy
implementation 'cn.tellyouwhat:gangsutils-all:${gangsutils.version}'
```

### logger

If you just want logger, add only `gangsutils-logger` dependency.

#### Quick Start

```scala
val logger = GangLogger()
logger.info("hello world")
```

You will see standard output as below:

```
[INFO] - hostname - 2021-07-20T14:45:20.425 - some.package.name.ClassName$#main line number 20: hello world
```

in English

or

```
【信息】 - hostname - 2021-07-20T14:45:20.425 - some.package.name.ClassName$#main第20行: hello world
```

in Chinese

Language is based on your system, retrieved by `Locale` default or you can set in the configuration file

#### Change logger style

All the properties are immutable, so they can not be change after the logger is initialized unless you re-initialize it.

To config a logger, you can:

1. config using pre-defined methods

  ```scala
  //enable or disable "package#method line number n" part in "[level] - hostname - datetime - package#method$: content"
GangLogger.enable / disableTrace()

//enable or disable "datetime$" part in "[level] - hostname - datetime - package#method$: content"
GangLogger.enable / disableDateTime()

//enable or disable "hostname" part in "[level] - hostname - datetime - package#method$: content"
GangLogger.enable / disableHostname()

//set logger default output destination, default is PRINTLN_LOGGER. PS: in Seq brackets press ctrl+shift+space, idea will prompt the available enumerations.
GangLogger.setDefaultLogDest(Seq(SupportedLogDest.PRINTLN_LOGGER, SupportedLogDest.WOA_WEBHOOK_LOGGER))

//enable all logger
GangLogger.setDefaultLogDest(SupportedLogDest.values.toSeq)

//control log levels, multiple values should be put in Array, orderd by knagene.ai.common.logger.SupportedLogDest enumerations order
//as below, "跟踪,信息" means PRINTLN_LOGGER level is trace, WOA_WEBHOOK_LOGGER level is info in Chinese, if your system language is English, use "TRACE,INFO" instead
//default is all trace
val l = "跟踪,信息"
GangLogger.setLogsLevels(l.split(",").map(LogLevel.withName)) // set PRINTLN_LOGGER trace, set WOA_WEBHOOK_LOGGER info, if there are other loggers undefined, an IllegalArgumentException will be throwed
//or
GangLogger.setLogsLevels(Array(LogLevel.TRACE, LogLevel.INFO)) // set PRINTLN_LOGGER trace, set WOA_WEBHOOK_LOGGER info, if there are other loggers undefined, an IllegalArgumentException will be throwed
//equivalent to
GangLogger.setLogsLevels(LogLevel.TRACE :: LogLevel.INFO :: Nil) // set PRINTLN_LOGGER trace, set WOA_WEBHOOK_LOGGER info, if there are other loggers undefined, an IllegalArgumentException will be throwed
//equivalent to
GangLogger.setLogsLevels(Map(SupportedLogDest.WOA_WEBHOOK_LOGGER -> LogLevel.INFO)) // set WOA_WEBHOOK_LOGGER info, trace for the rest

//reset static variables config to default
GangLogger.resetLoggerConfig()

//set a prefix for every log before content block
//eg: "package#method line number n" part in "[level] - hostname - datetime - package#method$: logPrefix - content"
GangLogger.setLogPrefix("a prefix")

//reset logPrefix to default empty string. Note that this only take effect when you create a new instance of GangLogger
GangLogger.clearLogPrefix()

//get an already exists(previously created) logger instance, or create a new one with GangLogger current static variables config
GangLogger.getLogger

//set stored logger variable to None
GangLogger.killLogger()
  ```

2. config when invoking apply

  ```scala
  val logger = GangLogger(isDTEnabled, isTraceEnabled, defaultLogDest, logsLevels, logPrefix, isHostnameEnabled)
  ```

#### log levels

There are **6 levels** in this util pack, trace、info、success、warning、error and critical. None of them throw exceptions,
you should throw it manually.

Other than pre-config log destination, you can set the-log-level log destination by `enabled` parameter, like

```scala
// means whatever destination you configured at before, this one log will be sent to parameter enabled specified destination.
logger.info("hello world")(enabled = Seq(SupportedLogDest.PRINTLN_LOGGER))
```

```scala
logger = GangLogger()
logger.trace("012")
logger.success("012")
logger.info("123")
logger.warning("234")
logger.error("345")
logger.critical("456")
```

#### Other supported log destination

We support

- println logger (print to stdout with ANSI color)
- [WOA](https://woa.wps.cn/) webhook logger
- [QYWX(企业微信)](https://work.weixin.qq.com/) webhook logger
- [DingTalk(钉钉)](https://www.dingtalk.com/) webhook logger
- [Slack](https://slack.com/) webhook logger
- [Telegram](https://telegram.org/) webhook logger
- [Feishu(飞书)](https://www.feishu.cn/) webhook logger
- [ServerChan(方糖Server酱)](https://sct.ftqq.com/) webhook logger

It is very simple to use, bear in mind to invoke `XXWebhookLogger.initializeXXWebhook(...)` before you create a new
GangLogger instance.

eg:

```scala
SlackWebhookLogger.initializeSlackUrls(slackWebhookURL)
val logger = GangLogger(defaultLogDest = Seq(SupportedLogDest.SLACK_WEBHOOK_LOGGER))
retry(2)(logger.info("slack webhook logger send a log into slack with correct url")) match {
  case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
  case Success(v) => v shouldBe true
}
```

For more examples, see
test <https://github.com/HarborZeng/gangsutils/tree/master/gangsutils-common/src/test/scala/cn/tellyouwhat/gangsutils/common/logger>

#### Full example

```scala
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.PRINTLN_LOGGER
import cn.tellyouwhat.gangsutils.logger.{GangLogger, LogLevel, Logger}
import cn.tellyouwhat.gangsutils.logger.helper.{Timeit, TimeitLogger}

class MyApp extends Timeit {

  private val logger: Logger = MyApp.logger

  override def run(desc: String): Unit = {
    logger.info("123")
  }

}

object MyApp {

  private implicit var logger: Logger = _

  def main(args: Array[String]): Unit = {
    GangLogger.setLogsLevels(Map(PRINTLN_LOGGER -> LogLevel.TRACE))
    GangLogger.disableTrace()
    logger = GangLogger(isTraceEnabled = true)
    logger.trace("tracing")

    MyApp().run()
  }

  def apply(): MyApp = new MyApp() with TimeitLogger
}
```

```
【跟踪】 - GANG-PC - 2021-08-27T13:11:19.549114400 - cn.tellyouwhat.gangsutils.logger.Logger#buildLog 第108行: tracing
【跟踪】 - GANG-PC - 2021-08-27T13:11:19.564740600 - cn.tellyouwhat.gangsutils.logger.Logger#buildLog 第108行: 开始任务
【信息】 - GANG-PC - 2021-08-27T13:11:19.564740600 - cn.tellyouwhat.gangsutils.logger.Logger#buildLog 第108行: 123
【成功】 - GANG-PC - 2021-08-27T13:11:19.564740600 - cn.tellyouwhat.gangsutils.logger.Logger#buildLog 第108行: 完成任务，耗时0s
```

In this example, we used `cn.tellyouwhat.gangsutils.logger.helper.{Timeit, TimeitLogger}` to implement an AOP logger to calculate the start and end(success or failure) of a function. You can use it too.

Just extends your class from `Timeit` and implement `run` method, and new your class with `TimeitLogger` to get aspect delegated.

### functions and helpers

If you just want functions and helpers which do not involve spark and hadoop, add only `gangsutils-core` dependency.

#### constants

There are a lot of strings, used to build log content and test.

#### functions

These functions could be useful in you are coding in Scala, see <https://github.com/HarborZeng/gangsutils/blob/master/gangsutils-core/src/test/scala/cn/tellyouwhat/gangsutils/core/funcsTest.scala> test case for use case.

#### helpers

There are a few helpers, like `ConfigReader`, `I18N`(internal use only) and `chaining`.

`chaining` is a good implicit object that can help you write scala code with less `val`s and `var`s.

It implements `tap` and `pipe` like in unix terminal system.

You can refer to <https://alvinalexander.com/scala/scala-2.13-pipe-tap-chaining-operations/> for example.

PS: Implementation in `chaining` is `|!` for `tap` and `|>` for `pipe`

eg,

<https://github.com/HarborZeng/gangsutils/blob/master/gangsutils-core/src/main/scala/cn/tellyouwhat/gangsutils/core/helper/chaining.scala>

### spark functions

If you just want functions and helpers which is spark related, add only `gangsutils-spark` dependency.

Spark function.

see <https://github.com/HarborZeng/gangsutils/blob/master/gangsutils-spark/src/test/scala/cn/tellyouwhat/gangsutils/spark/funcsTest.scala> for use cases.

## TODO

- [x] Make I18N private in gangsutils
- [x] Change `gangConfig.properties` file name to `gangsutilsConfig.properties`
- [x] Use yaml instead of properties file for configuration
- [x] Use system default language and region when `gangsutilsConfig.properties` is overwritten but `default-lang`
  or `default-region` are missing
- [x] Change `sendRequest`  parameter `queryStrings` to `form`
- [x] Extract `.replaceAll("""\e\[[\d;]*[^\d;]""", "")` to a method `stripANSIColor`
- [x] Change `Logger` `defaultLogDest` to `PRINTLN_LOGGER` to test whether `enabled` parameter works when logger is
  got by `GangLogger.getLogger`
- [x] Separate spark and hadoop tools into separate modules
- [x] Extract logger module from common module
- [x] Add FileLogger (by date)
- [ ] Add LogStash(Stream)Logger
- [x] Make hostname lazy
- [x] Make log a case class containing hostname, datetime, trace, content and etc, serialize it when using(println or
  send to webhook)
- [x] Different log configurations for different logs by default value and config file

## License

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FHarborZeng%2Fgangsutils.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FHarborZeng%2Fgangsutils?ref=badge_large)
