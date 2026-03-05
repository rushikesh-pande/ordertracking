package com.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Auto-added by Hub-Spoke Orchestrator v3.0
 * Repo: ordertracking — discovered via GitHub API scan (no URL provided)
 */
@Aspect @Component @Slf4j
public class PerformanceAspect {

    @Around("execution(* com.controller..*(..))"
           + " || execution(* com.service..*(..))")
    public Object timeMethod(ProceedingJoinPoint pjp) throws Throwable {
        long   t0  = System.currentTimeMillis();
        String mtd = pjp.getSignature().getDeclaringType().getSimpleName()
                     + "." + pjp.getSignature().getName();
        log.info("[HUB-SPOKE-v3] >>> {}", mtd);
        try {
            Object res = pjp.proceed();
            log.info("[HUB-SPOKE-v3] <<< {} done={}ms", mtd,
                     System.currentTimeMillis() - t0);
            return res;
        } catch (Exception ex) {
            log.error("[HUB-SPOKE-v3] ERR {} {}ms ex={}",
                      mtd, System.currentTimeMillis() - t0, ex.getMessage());
            throw ex;
        }
    }
}
