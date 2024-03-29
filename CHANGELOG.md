# Changelog

## 0.4.0

### Improvements

* Removed "class" from log message, since it's already present in the log name
* Made logging error trace enabled by default

## 0.3.2

### Improvements

* Improvements on error logs

### Bug fixes

* Log message don't respecting custom message value
* Exclude params isn't working

## 0.3.1

### Improvements

* Separated `ms` from `elapsedTime` value

## 0.3.0

### Features

* Allow to define a custom message per method
* Allow to exclude params for logging (values are anonymized)

## 0.2.1

### Bug fixes

* Correct argument values

## 0.2.0

### Features

* Concat param name and param value
* Allow to choose log or not method return
* Allow to configure specific log levels to exception class
* Return class name on log message

## 0.1.0

Refactoring structure and adding some customizations in Logging annotation

## 0.1.0-alpha-1

First Release.