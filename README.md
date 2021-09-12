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

see [gangsutils-logger 使用指南.md](https://github.com/HarborZeng/gangsutils/blob/develop/gangsutils-logger/gangsutils-logger%20%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97.md)

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
- [x] `.replace("\n", "\\n")`
- [x] async log
- [x] static members setup for each logger individually
- [x] stacktrace 

## License

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FHarborZeng%2Fgangsutils.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FHarborZeng%2Fgangsutils?ref=badge_large)
