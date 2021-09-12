# gangsutils-logger 使用指南

gangsutils-logger 模块提供的日志功能，包含且不限于

- 面向标准输出（stdout）的日志目的地
- 通过 webhook 发送到特定 app 内的日志目的地
    - [WOA](https://woa.wps.cn/) webhook logger
    - [QYWX(企业微信)](https://work.weixin.qq.com/) webhook logger
    - [DingTalk(钉钉)](https://www.dingtalk.com/) webhook logger
    - [Slack](https://slack.com/) webhook logger
    - [Telegram](https://telegram.org/) webhook logger
    - [Feishu(飞书)](https://www.feishu.cn/) webhook logger
    - [ServerChan(方糖Server酱)](https://sct.ftqq.com/) webhook logger
    - [PushPlus(推送加)](https://pushplus.hxtrip.com/) webhook logger
- 通过文件系统将日志以特定样式写入文件的日志目的地
    - local plain text logger
    - local html logger

其中， webhook 日志均支持多机器人发送。

## 极简使用

```scala
import cn.tellyouwhat.gangsutils.logger.GangLogger

object MyApp {
  def main(args: Array[String]): Unit = {
    val logger = GangLogger()
    logger.info("hello world")
  }
}
```

```
【信息】 - GANG-PC - 2021-09-12T10:38:17.037810400: hello world
```

默认情况下，`GangLogger()` 会使用

```scala
isDTEnabled = true,
isTraceEnabled = false,
isHostnameEnabled = true,
logPrefix = None,
logLevel = LogLevel.TRACE,
async = false,
```

参数作为日志配置，生成仅包含 `PRINTLN_LOGGER` 的 `GangLogger` 对象。

`GangLogger` 是可以针对多个日志对象进行包装使用的工具，如上代码所述，`GangLogger` 对象仅包含一个默认的  `PRINTLN_LOGGER` ，他还可以包含多个其他的日志对象（ `Logger` 子类）。

如果不需要使用该 `GangLogger` 工具，可以直接使用具体的日志类，如：

```scala
import cn.tellyouwhat.gangsutils.logger.LogLevel
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.dest.PrintlnLogger

object MyApp {
  def main(args: Array[String]): Unit = {
    PrintlnLogger.initializeConfiguration(LoggerConfiguration())
    val logger = PrintlnLogger()
    logger.info("hello world")
  }
}

```

```
【信息】 - GANG-PC - 2021-09-12T10:53:38.372166800: hello world
```

等同于

```scala
import cn.tellyouwhat.gangsutils.logger.LogLevel
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.dest.PrintlnLogger

object MyApp {
  def main(args: Array[String]): Unit = {
    val logger = PrintlnLogger(LoggerConfiguration())
    logger.info("hello world")
  }
}
```

类似的用法也适用于其他的日志对象（ `Logger` 子类）

## Webhook logger

如 `FeishuWebhookLogger` ，在使用之前需要执行初始化方法，指定 webhook 的 token

```scala
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.dest.webhook.FeishuWebhookLogger

object MyApp {
  def main(args: Array[String]): Unit = {
    FeishuWebhookLogger.initializeFeishuWebhook("040117de-7776-444b-ba61-9bbee3ad5e33")
    val logger = FeishuWebhookLogger(LoggerConfiguration())
    logger.info("hello world")
  }
}
```

![](https://tellyouwhat-static-1251995834.cos.ap-chongqing.myqcloud.com/images/2021/9/1631415938322.png)

`FeishuWebhookLogger` 还支持加签名的机器人，如

```scala
FeishuWebhookLogger.initializeFeishuWebhook("085380aa-4d07-4ecc-b17f-fbb978e1da72;BRH2wOO3SOi64Sw0wiMXtb")
```

只需在 webhook 的 token 后追加分号和密钥

![](https://tellyouwhat-static-1251995834.cos.ap-chongqing.myqcloud.com/images/2021/9/1631416129181.png)

或

```scala
FeishuWebhookLogger.initializeFeishuWebhook(Array(Array("085380aa-4d07-4ecc-b17f-fbb978e1da72", "BRH2wOO3SOi64Sw0wiMXtb")))
```

使用数组指定 token 和 密钥，同时这些 webhook logger，也支持多个机器人，中间用逗号分隔开来，如：

```scala
FeishuWebhookLogger.initializeFeishuWebhook("token1;secret1,token2;secret2")
```

```scala
FeishuWebhookLogger.initializeFeishuWebhook(Array(Array("token1", "secret1"), Array("token2", "secret2")))
```

所有的 webhook logger 均支持设置代理，如：

```scala
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.dest.webhook.TelegramWebhookLogger

object MyApp {
  def main(args: Array[String]): Unit = {
    TelegramWebhookLogger.setProxy("127.0.0.1", 6080)
    TelegramWebhookLogger.initializeTelegramWebhook("-541655508;1957795670:AAE8KlT0LFdbvgiG1TJlR2kPUKVXLrenDT8")
    val logger = TelegramWebhookLogger(LoggerConfiguration())
    logger.info("hello world")
  }
}

```

<img src="https://tellyouwhat-static-1251995834.cos.ap-chongqing.myqcloud.com/images/2021/9/1631417232273.png" style="zoom:50%;" />

同样的，也支持

```scala
TelegramWebhookLogger.clearProxy()
```

## Local file logger

### text format

```scala
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.dest.fs.LocalPlainTextLogger

object MyApp {
  def main(args: Array[String]): Unit = {
    LocalPlainTextLogger.setLogSavePath("text_logs/1.log")
    val logger = LocalPlainTextLogger(LoggerConfiguration())
    logger.info("hello world")
  }
}

```

或 

```scala
val logger = LocalPlainTextLogger(LoggerConfiguration(), "text_logs/1.log")
```

同时也支持

```scala
LocalPlainTextLogger.resetLogSavePath()
LocalPlainTextLogger.resetConfiguration()
```

执行完毕后在 `text_logs/1.log` 文件中，就会产生日志。

![](https://tellyouwhat-static-1251995834.cos.ap-chongqing.myqcloud.com/images/2021/9/1631418055392.png)

### html format

```scala
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.dest.fs.{LocalHtmlLogger, LocalPlainTextLogger}

object MyApp {
  def main(args: Array[String]): Unit = {
    val logger = LocalHtmlLogger(LoggerConfiguration(), "html_logs/1.html")
    logger.info("hello world")
  }
}
```

也支持和 `LocalPlainTextLogger` 相同的静态方法。

![](https://tellyouwhat-static-1251995834.cos.ap-chongqing.myqcloud.com/images/2021/9/1631418146068.png)

本地文件日志器支持将日志文件归档的功能，类似于 logback 的 rolling 功能，当日志文件大于某阈值时，自动将文件重命名为 以时间戳为结尾 的文件，并使用旧文件名创建文件并使用。

```scala
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.dest.fs.LocalHtmlLogger
import org.scalactic.TimesOnInt.convertIntToRepeater

object MyApp {
  def main(args: Array[String]): Unit = {
    val logger = LocalHtmlLogger(LoggerConfiguration(), "html_logs/2.html")
    20000 times logger.info("hello world")
  }
}

```

以上代码将 `logger.info("hello world")` 重复执行 20000 遍

![](https://tellyouwhat-static-1251995834.cos.ap-chongqing.myqcloud.com/images/2021/9/1631418677224.png)

默认的阈值大小是 1M，可以通过覆盖配置文件重写此值

在资源（ resources ）文件夹下，创建 `gangsutilsConfig.yaml`

```yaml
logger:
  lang: zh-hans
  fs:
    localFile:
      blockSize: 1048576 # 1M
      split: true
```

`blockSize` 单位是字节，当 `split` 也为 true 的时候，就会按照 `blockSize` 为阈值进行日志切分。

语言 `lang` 支持：zh-hans, zh-hant, en

## GangLogger 组合使用

```scala
import cn.tellyouwhat.gangsutils.logger.GangLogger
import cn.tellyouwhat.gangsutils.logger.SupportedLogDest.{LOCAL_HTML_LOGGER, LOCAL_PLAIN_TEXT_LOGGER, PRINTLN_LOGGER, WOA_WEBHOOK_LOGGER}
import cn.tellyouwhat.gangsutils.logger.cc.LoggerConfiguration
import cn.tellyouwhat.gangsutils.logger.dest.fs.{LocalHtmlLogger, LocalPlainTextLogger}
import cn.tellyouwhat.gangsutils.logger.dest.webhook.WoaWebhookLogger
import cn.tellyouwhat.gangsutils.logger.helper.{Timeit, TimeitLogger}


class MyApp extends Timeit {

  private implicit val logger: GangLogger = GangLogger.getLogger

  override def run(desc: String): Unit = {
    logger.info(desc)
  }
}

object MyApp {
  
  def main(args: Array[String]): Unit = {
    import GangLogger.blockToThunk
    GangLogger.setLoggerAndConfigurationAndInitBlock(Seq(
      PRINTLN_LOGGER -> (LoggerConfiguration(isTraceEnabled = true), {}),
      WOA_WEBHOOK_LOGGER -> (LoggerConfiguration(), WoaWebhookLogger.initializeWoaWebhook("a35a9ed09b9a7bb50dc5cc13c4cc20af")),
      LOCAL_HTML_LOGGER -> (LoggerConfiguration(), LocalHtmlLogger.setLogSavePath("html_log/3.html")),
      LOCAL_PLAIN_TEXT_LOGGER -> (LoggerConfiguration(), LocalPlainTextLogger.setLogSavePath("text_log/3.txt")),
    ))
    val logger = GangLogger()
    logger.trace("tracing")

    try {
      1 / 0
    } catch {
      case e: Exception => logger.error("执行1/0时出错", e)
    }

    MyApp().run()

  }

  def apply(): MyApp = new MyApp() with TimeitLogger
}

```

![](https://tellyouwhat-static-1251995834.cos.ap-chongqing.myqcloud.com/images/2021/9/1631419317596.png)

![](https://tellyouwhat-static-1251995834.cos.ap-chongqing.myqcloud.com/images/2021/9/1631419931084.png)

![](https://tellyouwhat-static-1251995834.cos.ap-chongqing.myqcloud.com/images/2021/9/1631419956432.png)

<img src="https://tellyouwhat-static-1251995834.cos.ap-chongqing.myqcloud.com/images/2021/9/1631419994189.png" style="zoom: 33%;" />

`GangLogger.setLoggerAndConfigurationAndInitBlock` api 接收一系列日志及其设定用来初始化。

其中，日志枚举代表此日志，元组中第一个值代表 `LoggerConfiguration` 对象，第二个值代表初始化此对象时需要执行的代码块，比如初始化 token、key或者重置某些参数。

当然，也可以在外面初始化这些参数：

```scala
WoaWebhookLogger.initializeWoaWebhook("a35a9ed09b9a7bb50dc5cc13c4cc20af")
LocalHtmlLogger.setLogSavePath("html_log/3.html")
LocalPlainTextLogger.setLogSavePath("text_log/3.txt")
GangLogger.setLoggerAndConfiguration(Seq(
  PRINTLN_LOGGER -> LoggerConfiguration(isTraceEnabled = true),
  WOA_WEBHOOK_LOGGER -> LoggerConfiguration(),
  LOCAL_HTML_LOGGER -> LoggerConfiguration(),
  LOCAL_PLAIN_TEXT_LOGGER -> LoggerConfiguration(),
))
val logger = GangLogger()
```

这样的话，如果 `GangLogger.setLoggerAndConfiguration` 有重复的 logger，比如第二个 `WOA_WEBHOOK_LOGGER`，他们将会使用同一个 token，这是没有意义的，使用 `GangLogger.setLoggerAndConfigurationAndInitBlock` 即可在填入的代码块中，适时调整 token。

## LoggerConfiguration

`LoggerConfiguration` 可以设置**日志样式**，如是否**显示时间**、是否显示**包名类名方法名文件名行号**，是否显示日志前缀，设定最低的**日志级别**，是否为**异步日志**。

> 日志级别从低到高依次为：TRACE、INFO、SUCCESS、WARNING、ERROR、CRITICAL

如最低的日志级别设为 WARNING，那么最终输出日志只有 WARNING、ERROR、CRITICAL 三个级别的。

如果启用异步日志，将会用 Future 来执行做日志的动作，如果 JVM 早早退出，那么日志可能不会成功。

## 其他技巧

### 1、使用 `enabled` 参数，指定此条日志使用的日志目的地

```scala
logger.trace("tracing")(enabled = Seq(PRINTLN_LOGGER))
```

这样的话，此条日志就只会使用 `PRINTLN_LOGGER` 输出。

### 2、日志会有 `Boolean` 类型的返回值

```scala
val result = logger.info("")
```

如果 `result` 为 `true` 则说明日志输出成功或日志是异步的，为 `false` 则说明日志输出失败或日志级别过低，如日志级别设为 WARNING，执行上述代码则会返回 `false`

如果 `logger` 为 `Logger` 对象，那么多个机器人必须全部返回 `true` 时，才会返回 `true`。

如果 `logger` 为 `GangLogger` 对象，那么多个符合日志级别的日志对象必须全部返回 `true` 时，才会返回 `true`。

### 3、GangLogger 有 getLogger 方法

`GangLogger` 里存有单例对象 `_logger`，所以

```scala
GangLogger.getLogger
```

获取到单例对象，如果未初始化，那么就初始化一个默认的 `logger`

```scala
GangLogger.killLogger()
```

将单例对象重置为 `None`

