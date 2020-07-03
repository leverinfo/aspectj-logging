# aspectj-logging

[![Maven Central](https://img.shields.io/maven-central/v/br.com.leverinfo/aspectj-logging.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22br.com.leverinfo%22%20AND%20a:%22aspectj-logging%22)

This library allows logs to be generated automatically for method inputs and outputs, in addition to exception logs.

## Requirements

- Java 1.7+
- Spring Framework

## How to Use

To start, add that Maven dependency:

```
<dependency>
  <groupId>br.com.leverinfo</groupId>
  <artifactId>aspectj-logging</artifactId>
  <version>0.1.0-alpha-1</version>
</dependency>
```

...configure a Spring Bean like this:

```
@Bean
public LoggingAspect getLogging() {
  return new LoggingAspect();
}
```

then, in your method, add the `@EnableLogging` annotation:

```
@EnableLogging  
public String sayHello() {
  return "Hello";
}
```

The console shoud show something like this:

```
2020-07-02 15:31:23.611 DEBUG 25908 --- [tp291203641-258] br.com.leverinfo.logging.Test   : Entering on method "sayHello()"
2020-07-02 15:31:23.611 DEBUG 25908 --- [tp291203641-258] br.com.leverinfo.logging.Test   : Leaving method "sayHello()" with result: Hello
```

## Known Limitations

- Currently it works just with Spring Beans
- It shows results just in DEBUG level

## Roadmap

- Make it works without Spring
- Create a log level configuration to show results
- Show method execution times
- Show methor arguments values
- Create unit tests
- Configure CI/CD pipeline

Your contribution is appreciated.
