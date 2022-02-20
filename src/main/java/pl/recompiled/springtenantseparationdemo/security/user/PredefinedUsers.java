package pl.recompiled.springtenantseparationdemo.security.user;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.recompiled.springtenantseparationdemo.security.user.dto.CreateUserDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Component
@ConfigurationProperties(prefix = "predefined")
public class PredefinedUsers {

    private List<PredefinedTenant> tenants;

    @Data
    static class PredefinedTenant {
        private UUID id;
        private String name;
        private PredefinedUser admin;

        Tenant getTenant() {
            return Tenant.of(id, name);
        }

        CreateUserDto getAdminUser() {
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
