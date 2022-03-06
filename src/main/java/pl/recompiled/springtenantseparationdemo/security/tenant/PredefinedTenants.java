package pl.recompiled.springtenantseparationdemo.security.tenant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pl.recompiled.springtenantseparationdemo.security.user.dto.CreateUserDto;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "predefined")
public class PredefinedTenants {

    private List<PredefinedTenant> tenants;

    @Data
    public static class PredefinedTenant {
        private String id;
        private String name;
        private PredefinedUser admin;

        public TenantId getId() {
            return TenantId.of(id);
        }

        public CreateUserDto getAdminUser() {
            return admin.toDto();
        }
    }

    @Data
    static class PredefinedUser {
        private String username;
        private String password;

        CreateUserDto toDto() {
            return new CreateUserDto(username, password);
        }
    }
}
