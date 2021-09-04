# gangsutils

| __Goal__                  | Badges                                                       |
| :------------------------ | :----------------------------------------------------------- |
| __Packages andReleases__ | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.tellyouwhat/gangsutils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.tellyouwhat/gangsutils) [![Sonatype Nexus](https://img.shields.io/nexus/r/cn.tellyouwhat/gangsutils?server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/releases/cn/tellyouwhat/gangsutils/) |
| __JavaDocumentation__    | [![javadoc](https://javadoc.io/badge2/cn.tellyouwhat/gangsutils-logger/javadoc.svg)](https://javadoc.io/doc/cn.tellyouwhat/gangsutils-logger) |
| __BuildStatus__          | [![develop build](https://github.com/HarborZeng/gangsutils/actions/workflows/build.yml/badge.svg?branch=develop)](https://github.com/HarborZeng/gangsutils/actions/workflows/build.yml) [![master build](https://github.com/HarborZeng/gangsutils/actions/workflows/master-build.yml/badge.svg?branch=master)](https://github.com/HarborZeng/gangsutils/actions/workflows/master-build.yml) |
| __JaCoCo TestCoverage__  | [![codecov](https://codecov.io/gh/HarborZeng/gangsutils/branch/master/graph/badge.svg?token=MUYXET5V4O)](https://codecov.io/gh/HarborZeng/gangsutils) |
| __License__               | [![License](https://img.shields.io/badge/License-Apache%20License%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) [![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FHarborZeng%2Fgangsutils.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FHarborZeng%2Fgangsutils?ref=badge_shield) |

## Utilities

Gangsutils stands for **Gang** **S**cala **Utils**, it includes several modules.

Replace `${gangsutils.version}` to the latest stable version showed in the nexus repository
badge [![Sonatype Nexus](https://img.shields.io/nexus/r/cn.tellyouwhat/gangsutils?server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/releases/cn/tellyouwhat/gangsutils/)

Note: Only import what you need to avoid unnecessary download.

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

All the properties are immutable, so they can not be change after the logger is instantiated unless you re-instantiate it.

To config a logger, you can:

1. config using pre-defined methods
    
    ```scala
    import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.DINGTALK_WEBHOOK_LOGGER
    import cn.tellyouwhat.gangsutils.logger.GangLogger
    import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
    
    GangLogger.setLoggerAndConfiguration(Map(
      DINGTALK_WEBHOOK_LOGGER -> LoggerConfiguration(isDTEnabled = true, isTraceEnabled = true, isHostnameEnabled = true, logPrefix = Some("prefix"), logLevel = LogLevel.TRACE)
    ))
    DingTalkWebhookLogger.initializeDingTalkWebhook("key;signSecret")
    
    val logger = GangLogger()
    logger.info("dingtalk webhook logger send a log into dingtalk with correct key and sign")
    
    //If you new GangLogger in this way:
    val logger = GangLogger(isDTEnabled = true, isTraceEnabled = true, isHostnameEnabled = true, logPrefix = Some("prefix"), logLevel = LogLevel.TRACE)
    //the parameters you filled in GangLogger apply method are for PrintlnLogger if the underlying logger2Configuration is null
    
    //get an already exists(previously created) logger instance, or create a new `PrintlnLogger` with default `LoggerConfiguration`
    GangLogger.getLogger
    
    //set stored _logger variable to None
    GangLogger.killLogger()
    
    //set stored logger2Configuration to null
    GangLogger.clearLogger2Configuration()
    ```

#### log levels

There are **6 levels** in this util pack, trace、info、success、warning、error and critical. None of them throw exceptions,
you should throw it manually if needed.

Other than pre-config log destination, you can set the-log-level log destination by `enabled` parameter, like

```scala
// means whatever destination you configured at before,
// this one log will be sent to parameter enabled specified destination, 
// which must be contained in the previously configured destination.
// That is to say if you use `GangLogger.setLoggerAndConfiguration` set DINGTALK_WEBHOOK_LOGGER and PRINTLN_LOGGER, then specify WOA_WEBHOOK_LOGGER in enabled parameter,
// then there will be no logger instance left in the underlying loggers seq, so nothing will be logged.
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
- [PushPlus(推送加)](https://pushplus.hxtrip.com/) webhook logger
- local plain text logger (write log to file, if file size reach threshold, move it with a timestamp tailing new name)
- local html logger (write log to file with colorful style, if file size reach threshold, move it with a timestamp
  tailing new name)

It is very simple to use, bear in mind to invoke `XXWebhookLogger.initializeXXWebhook(...)` before you create a new
GangLogger instance.

For `cn.tellyouwhat.gangsutils.logger.dest.fs.LocalHtmlLogger`
and `cn.tellyouwhat.gangsutils.logger.dest.fs.LocalPlainTextLogger`, additionally invoke `LocalXXXLogger.setLogSavePath`
before create a new GangLogger instance.

eg:

```scala
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.DINGTALK_WEBHOOK_LOGGER
import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration

GangLogger.setLoggerAndConfiguration(Map(
  SLACK_WEBHOOK_LOGGER -> LoggerConfiguration(),
  // more loggers here
))
SlackWebhookLogger.initializeSlackUrls(slackWebhookURL)
val logger = GangLogger()
retry(2)(logger.info("slack webhook logger send a log into slack with correct url")) match {
  case Failure(e) => a[SocketTimeoutException] should be thrownBy (throw e)
  case Success(v) => v shouldBe true
}
```

GangLogger uses reflection to create all the logger you filled in `setLoggerAndConfiguration` map parameter, and do the log action iteratively over all the loggers instance.

But if you just want one `PrintlnLogger`, directly use it 

```scala
// make sure no bother
GangLogger.clearLogger2Configuration()
// create default `PrintLogger` with default `LoggerConfiguration`
val logger = GangLogger(foo=bar, xxx)
```

By default, it is `PrintlnLogger` with default `LoggerConfiguration`

Plus, you can directly use XXLogger like

```scala
LocalPlainTextLogger.setLogSavePath(path)
LocalPlainTextLogger.initializeConfiguration(LoggerConfiguration())
val logger = LocalPlainTextLogger()
// which equivalent to
val logger = LocalHtmlLogger(LoggerConfiguration(), path)

logger.asInstanceOf[LocalPlainTextLogger].logSavePath shouldBe Paths.get(path)
```

For more examples, see test <https://github.com/HarborZeng/gangsutils/tree/master/gangsutils-logger/src/test/scala/cn/tellyouwhat/gangsutils/logger/dest>

#### Full example

```scala
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.PRINTLN_LOGGER
import cn.tellyouwhat.gangsutils.logger.{GangLogger, LogLevel, Logger}
import cn.tellyouwhat.gangsutils.logger.helper.{Timeit, TimeitLogger}

class MyApp extends Timeit {

  private val logger: GangLogger = MyApp.logger

  override def run(desc: String): Unit = {
    logger.info("123")
  }

}

object MyApp {

  private implicit var logger: GangLogger = _

  def main(args: Array[String]): Unit = {
    logger = GangLogger(isTraceEnabled = true)
    logger.trace("tracing")

    MyApp().run()
  }

  def apply(): MyApp = new MyApp() with TimeitLogger
}

```

```
【跟踪】 - GANG-PC - 2021-08-27T13:11:19.549114400 - cn.tellyouwhat.gangsutils.logger.Logger#buildLog 第108行: tracing
...more output ...
```

In this example, we used `cn.tellyouwhat.gangsutils.logger.helper.{Timeit, TimeitLogger}` to implement an AOP logger to
calculate the start and end(success or failure) of a function. You can use it elsewhere too.

Just extends your class from `Timeit` and implement `run` method, and new your class with `TimeitLogger` to get aspect
delegated.

### functions and helpers

If you just want functions and helpers which do not involve spark and hadoop, add only `gangsutils-core` dependency.

#### constants

There are a lot of strings, used to build log content and test.

#### functions

These functions could be useful in you are coding in Scala,
see <https://github.com/HarborZeng/gangsutils/blob/master/gangsutils-core/src/test/scala/cn/tellyouwhat/gangsutils/core/funcsTest.scala>
test case for use case.

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

see <https://github.com/HarborZeng/gangsutils/blob/master/gangsutils-spark/src/test/scala/cn/tellyouwhat/gangsutils/spark/funcsTest.scala>
for use cases.

## TODO

- [x] Make I18N private in gangsutils
- [x] Change `gangConfig.properties` file name to `gangsutilsConfig.properties`
- [x] Use yaml instead of properties file for configuration
- [x] Use system default language and region when `gangsutilsConfig.properties` is overwritten but `default-lang`
  or `default-region` are missing
- [x] Change `sendRequest`  parameter `queryStrings` to `form`
- [x] Extract `.replaceAll("""\e\[[\d;]*[^\d;]""", "")` to a method `stripANSIColor`
- [x] Change `Logger` `defaultLogDest` to `PRINTLN_LOGGER` to test whether `enabled` parameter works when logger is got
  by `GangLogger.getLogger`
- [x] Separate spark and hadoop tools into separate modules
- [x] Extract logger module from common module
- [x] Add FileLogger (by date)
- [ ] Add LogStash(Stream)Logger
- [x] Make hostname lazy
- [x] Make log a case class containing hostname, datetime, trace, content and etc, serialize it when using(println or
  send to webhook)
- [x] Different log configurations for different logs by default value and config file
- [x] Add proxy settings for TelegramWebhookLogger

## License

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FHarborZeng%2Fgangsutils.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FHarborZeng%2Fgangsutils?ref=badge_large)
