package br.com.leverinfo.logging;

import java.lang.reflect.Method;
import java.util.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * @author franciscosousabr
 */
@Aspect
public class LoggingAspect {

  private final Map<Class<? extends Exception>, Level> exceptionsLogLevels = new HashMap<>();

  /**
   * Set a specific log level to an exception type
   *
   * @author franciscosousabr
   * @param exceptionType Exception type
   * @param logLevel Log level
   */
  public void addExceptionLogLevel(Class<? extends Exception> exceptionType, Level logLevel) {
    exceptionsLogLevels.put(exceptionType, logLevel);
  }

  @Around("@annotation(br.com.leverinfo.logging.Logging)")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    final Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

    final long start = System.currentTimeMillis();
    final Object result = joinPoint.proceed();
    final long elapsed = System.currentTimeMillis() - start;

    MessageParams messageParams = buildMessageParams(joinPoint, result, elapsed);
    switch (messageParams.getLevel()) {
      case ERROR:
        logger.error(messageParams.getMessage(), messageParams.getValues());
        break;
      case WARN:
        logger.warn(messageParams.getMessage(), messageParams.getValues());
        break;
      case INFO:
        logger.info(messageParams.getMessage(), messageParams.getValues());
        break;
      case TRACE:
        logger.trace(messageParams.getMessage(), messageParams.getValues());
        break;
      default:
        logger.debug(messageParams.getMessage(), messageParams.getValues());
    }

    return result;
  }

  @AfterThrowing(
      pointcut = "execution(* *(..)) && @annotation(br.com.leverinfo.logging.Logging)",
      throwing = "error")
  public void afterThrowing(JoinPoint joinPoint, Throwable error) {
    final Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

    final Method method = getMethod(joinPoint);
    final Logging annotation = method.getAnnotation(Logging.class);
    if (annotation.logError()) {
      final String message =
          getCustomMessage(joinPoint)
              + String.format(
                  "class=%s method=%s(%s) errorType=%s errorMessage=%s",
                  joinPoint.getTarget().getClass().getSimpleName(),
                  method.getName(),
                  buildArguments(joinPoint),
                  error.getClass().getSimpleName().replace("Exception", ""),
                  error.getMessage());

      Level errorLevel = exceptionsLogLevels.getOrDefault(error.getClass(), Level.ERROR);
      if (annotation.logErrorTrace()) {
        switch (errorLevel) {
          case ERROR:
            logger.error(message, error);
            break;
          case WARN:
            logger.warn(message, error);
            break;
          case INFO:
            logger.info(message, error);
            break;
          case TRACE:
            logger.trace(message, error);
            break;
          default:
            logger.debug(message, error);
        }
      } else {
        switch (errorLevel) {
          case ERROR:
            logger.error(message);
            break;
          case WARN:
            logger.warn(message);
            break;
          case INFO:
            logger.info(message);
            break;
          case TRACE:
            logger.trace(message);
            break;
          default:
            logger.debug(message);
        }
      }
    }
  }

  private static Method getMethod(JoinPoint joinPoint) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    return methodSignature.getMethod();
  }

  private static String getCustomMessage(JoinPoint joinPoint) {
    final Method method = getMethod(joinPoint);
    final Logging annotation = method.getAnnotation(Logging.class);

    return !"".equals(annotation.message()) ? annotation.message() + " " : "";
  }

  private static MessageParams buildMessageParams(
      JoinPoint joinPoint, Object result, long elapsed) {
    final Method method = getMethod(joinPoint);
    final Logging annotation = method.getAnnotation(Logging.class);

    List<String> messageParts = new ArrayList<>();
    List<Object> messageValues = new ArrayList<>();

    messageParts.add("class={}");
    messageValues.add(joinPoint.getTarget().getClass().getSimpleName());

    messageParts.add("method={}({})");
    messageValues.add(method.getName());
    messageValues.add(buildArguments(joinPoint));

    if (annotation.logReturn() && !method.getReturnType().equals(Void.class)) {
      messageParts.add("return={}");
      messageValues.add(getResult(method, result));
    }

    messageParts.add("elapsedTime={}ms");
    messageValues.add(elapsed);

    return new MessageParams(
        annotation.level(), String.join(" ", messageParts), messageValues.toArray());
  }

  private static String buildArguments(JoinPoint joinPoint) {
    final Method method = getMethod(joinPoint);
    final Logging annotation = method.getAnnotation(Logging.class);

    CodeSignature signature = (CodeSignature) joinPoint.getSignature();
    List<String> arguments = new ArrayList<>();
    for (int i = 0; i < signature.getParameterNames().length; i++) {
      arguments.add(
          signature.getParameterNames()[i]
              + "="
              + (Arrays.asList(annotation.excludeParams()).contains(i)
                  ? joinPoint.getArgs()[i].toString().replaceAll(".", "*")
                  : joinPoint.getArgs()[i]));
    }
    return String.join(", ", arguments);
  }

  private static Object getResult(Method method, Object result) {
    return !method.getReturnType().equals(Void.TYPE) ? result : "void";
  }

  private static class MessageParams {
    private final Level level;
    private final String message;
    private final Object[] values;

    public MessageParams(Level level, String message, Object[] values) {
      this.level = level;
      this.message = message;
      this.values = values;
    }

    public Level getLevel() {
      return level;
    }

    public String getMessage() {
      return message;
    }

    public Object[] getValues() {
      return values;
    }
  }
}
