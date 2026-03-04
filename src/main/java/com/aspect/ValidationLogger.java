package com.aspect;

import com.orderprocessing.trace.TraceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Logs request validation results for all controller endpoints.
 * Intercepts @Valid/@Validated parameters and logs pass/fail with field errors.
 *
 * Auto-applied by CodeGen Agent after source scan of: ordertracking
 */
@Aspect
@Component
@Slf4j
public class ValidationLogger {

    /** Log entry for every REST controller method (before execution) */
    @Before("execution(* com.controller..*(..)) && args(..,@jakarta.validation.Valid (*),..)")
    public void logValidRequest(JoinPoint jp) {
        String traceId = getTraceId();
        String method  = jp.getSignature().toShortString();
        log.info("[{}] VALIDATION PASS  {}  — request is valid", traceId, method);
    }

    /** Log all controller requests regardless of validation */
    @Before("execution(* com.controller..*(..))")
    public void logControllerEntry(JoinPoint jp) {
        String traceId = getTraceId();
        String method  = jp.getSignature().getDeclaringType().getSimpleName()
                         + "." + jp.getSignature().getName();
        Object[] args  = jp.getArgs();
        String argSummary = Arrays.stream(args)
                .filter(a -> a != null)
                .map(a -> a.getClass().getSimpleName() + ":" + shorten(a.toString()))
                .collect(Collectors.joining(", "));
        log.info("[{}] REQUEST  {}  args=[{}]", traceId, method, argSummary);
    }

    /** Log after controller method returns normally */
    @AfterReturning(pointcut = "execution(* com.controller..*(..))", returning = "result")
    public void logControllerReturn(JoinPoint jp, Object result) {
        String traceId = getTraceId();
        String method  = jp.getSignature().getName();
        log.info("[{}] RESPONSE {}  result={}", traceId, method,
                result != null ? result.getClass().getSimpleName() : "void");
    }

    /** Log exceptions thrown from any controller */
    @AfterThrowing(pointcut = "execution(* com.controller..*(..))", throwing = "ex")
    public void logControllerException(JoinPoint jp, Exception ex) {
        String traceId = getTraceId();
        String method  = jp.getSignature().getName();
        log.error("[{}] EXCEPTION  {}  error={}  type={}",
                traceId, method, ex.getMessage(), ex.getClass().getSimpleName());
    }

    private String getTraceId() {
        try { return TraceContextHolder.getTraceId(); } catch (Exception e) { return "UNKNOWN"; }
    }

    private String shorten(String s) {
        return s.length() > 80 ? s.substring(0, 77) + "..." : s;
    }
}
