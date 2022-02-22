package pl.recompiled.springtenantseparationdemo.security.user;

import org.springframework.security.core.context.SecurityContextHolder;

public class TenantContext {

    public static String getTenantId() {
        final TenantAdherent principal = (TenantAdherent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getTenantId();
    }
}
