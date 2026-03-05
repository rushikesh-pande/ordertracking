package com.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Auto-added by Hub-Spoke Orchestrator v2.0
 * Repo: ordertracking — discovered via GitHub API scan (no URL provided)
 */
@Aspect @Component @Slf4j
public class PerformanceAspect {
    @Around("execution(* com.controller..*(..))")
    public Object timeController(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String method = pjp.getSignature().getDeclaringType().getSimpleName()
                        + "." + pjp.getSignature().getName();
        log.info("[HUB-SPOKE] ENTER  {}", method);
        try {
            Object result = pjp.proceed();
            log.info("[HUB-SPOKE] EXIT   {}  duration={}ms", method, System.currentTimeMillis()-start);
            return result;
        } catch (Exception ex) {
            log.error("[HUB-SPOKE] ERROR  {}  duration={}ms  ex={}", method, System.currentTimeMillis()-start, ex.getMessage());
            throw ex;
        }
    }
}
