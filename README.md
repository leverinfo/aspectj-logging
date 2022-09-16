# aspectj-logging

[![Maven Central](https://img.shields.io/maven-central/v/br.com.leverinfo/aspectj-logging.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22br.com.leverinfo%22%20AND%20a:%22aspectj-logging%22)

This library allows logs to be generated automatically for method inputs and outputs, in addition to
exception logs.

## Requirements

- Java 1.7+
- Spring Framework

## How to Use

To start, add that Maven dependency:

```
<dependency>
  <groupId>br.com.leverinfo</groupId>
  <artifactId>aspectj-logging</artifactId>
  <version>0.1.0</version>
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
@Logging  
public String sayHello() {
  return "Hello";
}
```

The console shoud show something like this:

```
2022-09-16 15:31:23.611 DEBUG 25908 --- [tp291203641-258] br.com.leverinfo.logging.Test   : method=sayHello([]) return=Hello elapsedTime=1ms
```

Logging annotation allows some configurations:

- `level` | Set which log level shoud be used | Default: `Level.DEBUG`
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