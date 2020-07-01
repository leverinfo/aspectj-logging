package br.com.leverinfo;

import java.lang.reflect.Method;
import org.apache.commons.lang3.exception.ExceptionUtils;
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

  @Around("@annotation(br.com.leverinfo.EnableLogging)")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

    Method invokedMethod = getMethod(joinPoint);
    String parametersTypes = getMethodParametersTypes(invokedMethod);
    logger.debug("Entering on method \"{}({})\"", invokedMethod.getName(), parametersTypes);

    Object result = joinPoint.proceed();

    logger.debug("Exiting method \"{}({})\"", invokedMethod.getName(), parametersTypes);

    return result;
  }

  @AfterThrowing(
      pointcut = "execution(* *(..)) && @annotation(br.com.leverinfo.EnableLogging)",
      throwing = "error")
  public void afterThrowingAnyException(JoinPoint joinPoint, Throwable error) {
    Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

    Method invokedMethod = getMethod(joinPoint);
    String parametersValues = getMethodParametersValues(joinPoint);
    logger.error(
        "An exception was thrown while trying execution method \"{}({})\"",
        invokedMethod.getName(),
        parametersValues);
    logger.error(
        "Exception: {}%nMessage: {}%nStack Trace: {}",
        error.getClass().getSimpleName(), error.getMessage(), ExceptionUtils.getStackTrace(error));
  }

  private Method getMethod(JoinPoint joinPoint) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    return methodSignature.getMethod();
  }

  private String getMethodParametersTypes(Method method) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < method.getParameterTypes().length; i++) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(method.getParameterTypes()[i].getSimpleName());
    }

    return sb.toString();
  }

  private String getMethodParametersValues(JoinPoint joinPoint) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < joinPoint.getArgs().length; i++) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(joinPoint.getArgs()[i]);
    }

    return sb.toString();
  }
}
