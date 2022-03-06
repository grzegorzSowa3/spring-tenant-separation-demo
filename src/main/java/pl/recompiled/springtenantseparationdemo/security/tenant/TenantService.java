package pl.recompiled.springtenantseparationdemo.security.tenant;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.recompiled.springtenantseparationdemo.security.tenant.PredefinedTenants.PredefinedTenant;
import pl.recompiled.springtenantseparationdemo.security.user.UserService;
import pl.recompiled.springtenantseparationdemo.security.user.dto.CreateTenantDto;
import pl.recompiled.springtenantseparationdemo.security.user.dto.TenantData;

@Service
public class TenantService {

    private final UserService userService;
    private final TenantRepository tenantRepository;

    public TenantService(UserService userService,
                         PredefinedTenants predefinedTenants,
                         TenantRepository tenantRepository
    ) {
        this.userService = userService;
        this.tenantRepository = tenantRepository;
        createAdmins(predefinedTenants);
    }

    public TenantData createTenant(CreateTenantDto dto) {
        final Tenant tenant = tenantRepository.save(Tenant.newInstance(dto.getName()));
        try {
            TenantContext.override(tenant.getId());
            userService.createAdmin(dto.getAdmin());
        } finally {
            TenantContext.reset();
        }
        return tenant.toData();
    }

    public void createAdmins(PredefinedTenants predefinedTenants) {
        predefinedTenants.getTenants()
                .forEach(this::createAdminForTenant);
    }

    public void createAdminForTenant(PredefinedTenant tenant) {
        try {
            userService.loadUserByUsername(tenant.getAdminUser().getUsername());
        } catch (UsernameNotFoundException e) {
            try {
                TenantContext.override(tenant.getId());
                userService.createAdmin(tenant.getAdminUser());
            } finally {
                TenantContext.reset();
            }
        }
    }
}
