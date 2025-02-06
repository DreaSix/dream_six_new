package com.dream.six.config;

import com.dream.six.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime startTime = LocalDateTime.now();
        String userId = MDC.get(Constants.USER_UUID_ATTRIBUTE);  // Retrieve the userId from MDC

        log.info("Execution of {} started at: {} by user: {}", joinPoint.getSignature(), startTime, userId);

        Object proceed = joinPoint.proceed(); // Proceed with the method execution

        LocalDateTime endTime = LocalDateTime.now();
        log.info("Execution of {} ended at: {} by user: {}", joinPoint.getSignature(), endTime, userId);

        long duration = ChronoUnit.SECONDS.between(startTime, endTime);
        log.info("Total duration for {}: {} seconds by user: {}", joinPoint.getSignature(), duration, userId);

        return proceed;
    }
}
