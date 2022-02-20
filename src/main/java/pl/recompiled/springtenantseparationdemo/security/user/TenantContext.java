package pl.recompiled.springtenantseparationdemo.security.user;

import org.springframework.security.core.context.SecurityContextHolder;

public class TenantContext {

    private static final ThreadLocal<String> overrideTenantId = new InheritableThreadLocal<>();

    public static String getTenantId() {
        if (overrideTenantId.get() == null) {
            final TenantAdherent principal = (TenantAdherent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return principal.getTenantId();
        } else {
            return overrideTenantId.get();
        }
    }

    public static void override(String tenantId) {
        overrideTenantId.set(tenantId);
    }

    public static void reset() {
        overrideTenantId.set(null);
    }
}
