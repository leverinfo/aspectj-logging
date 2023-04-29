# aspectj-logging

[![Maven Central](https://img.shields.io/maven-central/v/br.com.leverinfo/aspectj-logging.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22br.com.leverinfo%22%20AND%20a:%22aspectj-logging%22)

This library allows logs to be generated automatically for method inputs and outputs, in addition to
exception logs.

## Requirements

- Java 1.8+
- Spring Framework

## How to Use

To start, add that Maven dependency:

```
<dependency>
  <groupId>br.com.leverinfo</groupId>
  <artifactId>aspectj-logging</artifactId>
  <version>0.2.0</version>
</dependency>
```

...configure a Spring Bean like this:

```
@Bean
public LoggingAspect getLogging() {
  return new LoggingAspect();
}
```

then, in your method, add the `@Logging` annotation:

```
public class Foo {
  
  @Logging  
  public String sayAny(String word) {
    return "Saying " + word;
  }
}
```

The console shoud show something like this:

```
2023-04-29 14:53:23.611 DEBUG 25908 --- [      main] br.com.leverinfo.logging.Test   : class=Foo method=sayHello([word=Hello]) return=Saying Hello elapsedTime=1ms
```

## Configurations

### Specifying log levels for exceptions

It is possible to configure which log level should to use for a given exception class.
For this, it is necessary configure the `LoggingAspect` like this:

```
@Bean
public LoggingAspect getLogging() {
  LogginAspect loggingAspect = new LoggingAspect();
  loggingAspect.addExceptionLogLevel(MyException.class, Level.INFO);
  return loggingAspect;
}
```

### Configurations for method

Logging annotation allows some configurations:

- `level` | Set which log level shoud be used | Default: `Level.DEBUG`
- `logReturn` | Define if return value should be logged | Default: `true`
- `logError` | Define if errors should be logged | Default: `true`
- `logErrorTrace` | Define if complete stack trace should be logged. This property works
  together `logError` property, what means that if `logError` is `false`, this property has no
  effect

## Known Limitations

- Currently it works just with Spring Beans

## Roadmap

- Make it works without Spring
- Create unit tests
- Configure CI/CD pipeline

Your contribution is appreciated.