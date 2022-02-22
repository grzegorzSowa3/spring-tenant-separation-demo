package pl.recompiled.springtenantseparationdemo.security;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import pl.recompiled.springtenantseparationdemo.security.user.TenantContext;

import javax.persistence.EntityManager;

@Aspect
@Component
@RequiredArgsConstructor
public class TenantAwareFilterAspect {

    private final EntityManager em;

    @Around("@annotation(TenantAware)")
    public Object enableClientFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        final Session session = (Session) em.getDelegate();
        try {
            if (session.isOpen()) {
                session.enableFilter("tenant_aware_filter")
                        .setParameter("tenant_id", TenantContext.getTenantId());
            }
            return joinPoint.proceed();
        } finally {
            if (session.isOpen()) {
                session.disableFilter("tenant_aware_filter");
            }
        }
    }
}
