package pl.recompiled.springtenantseparationdemo.security.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.recompiled.springtenantseparationdemo.security.user.dto.CreateTenantDto;
import pl.recompiled.springtenantseparationdemo.security.user.dto.TenantData;

import java.util.List;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
class TenantEndpoint {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<GetTenantsResponse> getTenants() {
        return ResponseEntity.ok(new GetTenantsResponse(userService.getTenants()));
    }

    @Data
    @AllArgsConstructor
    private static class GetTenantsResponse {
        private List<TenantData> tenants;
    }

    @PostMapping
    public ResponseEntity<TenantData> createTenant(@RequestBody CreateTenantDto dto) {
        final TenantData tenant = userService.createTenant(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tenant);
    }
}
