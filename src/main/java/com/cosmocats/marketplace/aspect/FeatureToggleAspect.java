package com.cosmocats.marketplace.aspect;

import com.cosmocats.marketplace.exception.FeatureNotAvailableException;
import com.cosmocats.marketplace.service.FeatureToggleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class FeatureToggleAspect {

    private final FeatureToggleService featureToggleService;

    @Around("@annotation(featureToggle)")
    public Object checkFeatureToggle(ProceedingJoinPoint joinPoint, FeatureToggle featureToggle) throws Throwable {
        String featureName = featureToggle.value();
        if (!featureToggleService.isFeatureEnabled(featureName)) {
            log.warn("Blocked execution of [{}] because feature [{}] is disabled",
                    joinPoint.getSignature().toShortString(), featureName);
            throw new FeatureNotAvailableException(featureName);
        }
        return joinPoint.proceed();
    }
}
