package pl.recompiled.springtenantseparationdemo.security.tenant;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class TenantContext {

    private static final ThreadLocal<TenantId> tenantId = new ThreadLocal<>();

    static TenantId getTenantId() {
        if (TenantContext.tenantId.get() != null) {
            return TenantContext.tenantId.get();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return ((TenantAdherent) authentication.getPrincipal()).getTenantId();
        } else {
            return TenantId.any();
        }
    }

    public static void override(TenantId tenantId) {
        TenantContext.tenantId.set(tenantId);
    }

    public static void reset() {
        TenantContext.tenantId.remove();
    }
}
