package br.com.leverinfo.logging;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LoggingAspect {

  @Around("@annotation(br.com.leverinfo.logging.Logging)")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    final Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

    final Method method = getMethod(joinPoint);
    final Logging annotation = method.getAnnotation(Logging.class);

    final long start = System.currentTimeMillis();
    final Object result = joinPoint.proceed();
    final long elapsed = System.currentTimeMillis() - start;

    final String message = "method={}({}) return={} elapsedTime={}ms";

    switch (annotation.level()) {
      case ERROR:
        logger.error(
            message, method.getName(), joinPoint.getArgs(), getResult(method, result), elapsed);
        break;
      case WARN:
        logger.warn(
            message, method.getName(), joinPoint.getArgs(), getResult(method, result), elapsed);
        break;
      case INFO:
        logger.info(
            message, method.getName(), joinPoint.getArgs(), getResult(method, result), elapsed);
        break;
      case TRACE:
        logger.trace(
            message, method.getName(), joinPoint.getArgs(), getResult(method, result), elapsed);
        break;
      default:
        logger.debug(
            message, method.getName(), joinPoint.getArgs(), getResult(method, result), elapsed);
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
          String.format(
              "method=%s(%s) errorType=%s errorMessage=%s",
              method.getName(),
              Arrays.toString(joinPoint.getArgs()),
              error.getClass().getSimpleName().replace("Exception", ""),
              error.getMessage());

      if (annotation.logErrorTrace()) {
        logger.error(message, error);
      } else {
        logger.error(message);
      }
    }
  }

  private Method getMethod(JoinPoint joinPoint) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    return methodSignature.getMethod();
  }

  private Object getResult(Method method, Object result) {
    return !method.getReturnType().equals(Void.TYPE) ? result : "void";
  }
}
