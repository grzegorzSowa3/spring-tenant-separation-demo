package pl.recompiled.springtenantseparationdemo.security.tenant;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.recompiled.springtenantseparationdemo.security.user.dto.CreateTenantDto;
import pl.recompiled.springtenantseparationdemo.security.user.dto.TenantData;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
class TenantEndpoint {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantData> createTenant(@RequestBody CreateTenantDto dto) {
        final TenantData tenant = tenantService.createTenant(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tenant);
    }
}
