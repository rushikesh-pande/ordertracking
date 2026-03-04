package com.aspect;

import com.orderprocessing.trace.TraceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.util.Arrays;

/**
 * Spring AOP aspect that automatically measures and logs execution time
 * for all REST controller and service methods.
 *
 * Auto-discovered & applied by CodeGen Agent — no URL was provided.
 * Package auto-detected from existing source: com
 *
 * Usage: automatic — no annotations required.
 * Log pattern: [traceId] ClassName.methodName completed in Xms
 */
@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    /**
     * Around advice for all controller methods.
     * Logs: entry, request args, execution time, and any exception.
     */
    @Around("execution(* com.controller..*(..))")
    public Object timeController(ProceedingJoinPoint pjp) throws Throwable {
        return measureAndLog(pjp, "CONTROLLER");
    }

    /**
     * Around advice for all service methods.
     */
    @Around("execution(* com.service..*(..))")
    public Object timeService(ProceedingJoinPoint pjp) throws Throwable {
        return measureAndLog(pjp, "SERVICE");
    }

    private Object measureAndLog(ProceedingJoinPoint pjp, String layer) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        String traceId  = getTraceId();
        String method   = sig.getDeclaringType().getSimpleName() + "." + sig.getName();
        Object[] args   = pjp.getArgs();

        log.info("[{}] [{}] ENTER {}  args=({})  ",
                traceId, layer, method,
                args.length > 0 ? summariseArgs(args) : "none");

        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            long elapsed  = System.currentTimeMillis() - start;
            log.info("[{}] [{}] EXIT  {}  duration={}ms  status=OK",
                    traceId, layer, method, elapsed);
            return result;
        } catch (Exception ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[{}] [{}] EXIT  {}  duration={}ms  status=ERROR  exception={}",
                    traceId, layer, method, elapsed, ex.getMessage());
            throw ex;
        }
    }

    private String getTraceId() {
        try { return TraceContextHolder.getTraceId(); }
        catch (Exception e) { return "UNKNOWN"; }
    }

    private String summariseArgs(Object[] args) {
        return Arrays.stream(args)
                .map(a -> a == null ? "null" : a.getClass().getSimpleName())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }
}
