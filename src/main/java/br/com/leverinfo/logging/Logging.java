package br.com.leverinfo.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.slf4j.event.Level;

/**
 * @author franciscosousabr
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {

  /**
   * Set which log level shoud be used
   *
   * <p>Default: Level.DEBUG
   */
  Level level() default Level.DEBUG;

  /**
   * Define if return value should be logged
   *
   * <p>Default: true
   */
  boolean logReturn() default true;

  /**
   * Define if errors should be logged
   *
   * <p>Default: true
   */
  boolean logError() default true;

  /**
   * Define if complete stack trace should be logged. This property works together "logError"
   * property, what means that if "logError" is false, this property has no effect
   *
   * <p>Default: false
   */
  boolean logErrorTrace() default false;
}
