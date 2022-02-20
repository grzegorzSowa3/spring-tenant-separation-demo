package pl.recompiled.springtenantseparationdemo.security.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.recompiled.springtenantseparationdemo.security.user.PredefinedUsers.PredefinedTenant;
import pl.recompiled.springtenantseparationdemo.security.user.dto.CreateTenantDto;
import pl.recompiled.springtenantseparationdemo.security.user.dto.CreateUserDto;
import pl.recompiled.springtenantseparationdemo.security.user.dto.TenantData;
import pl.recompiled.springtenantseparationdemo.security.user.dto.UserData;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService implements UserDetailsService {

    private final Map<UUID, Tenant> predefinedTenants;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(PredefinedUsers predefinedUsers,
                       TenantRepository tenantRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.predefinedTenants = predefinedUsers.getTenants().stream()
                .collect(Collectors.toMap(PredefinedTenant::getId, PredefinedTenant::getTenant));
        predefinedUsers.getTenants()
                .forEach(tenant -> createAdminIfNotExists(tenant.getId().toString(), tenant.getAdminUser()));
    }

    public TenantData createTenant(CreateTenantDto dto) {
        final Tenant tenant = tenantRepository.save(Tenant.newInstance(dto.getName()));
        createUserWithAuthorities(tenant.getId().toString(), dto.getAdmin(), Set.of(Authority.ADMIN));
        return tenant.toData();
    }

    public UserData createUser(CreateUserDto dto) {
        return createUserWithAuthorities(TenantContext.getTenantId(), dto, Set.of(Authority.USER));
    }

    private void createAdminIfNotExists(String tenantId, CreateUserDto dto) {
        final Optional<User> admin = userRepository.findByUsername(dto.getUsername());
        if (admin.isEmpty()) {
            createUserWithAuthorities(tenantId, dto, Set.of(Authority.ADMIN));
        } else if (isTenantAdmin(admin.get(), tenantId)) {
            throw new PredefinedAdminUsernameInvalidException("Admin username duplicate: " + admin.get().getUsername());
        }
    }

    public static class PredefinedAdminUsernameInvalidException extends RuntimeException {
        public PredefinedAdminUsernameInvalidException(String message) {
            super(message);
        }
    }

    private boolean isTenantAdmin(User user, String tenantId) {
        return user.getAuthorities().contains(Authority.ADMIN) && user.getTenantId().equals(tenantId);
    }

    private UserData createUserWithAuthorities(String tenantId, CreateUserDto dto, Set<Authority> authorities) {
        final User user = User.newInstance(
                tenantId,
                dto.getUsername(),
                passwordEncoder.encode(dto.getPassword()),
                authorities);
        return userRepository.save(user).toData();
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user with such username!"));
    }

    public void deleteUser(String userId) {
        userRepository.findById(UUID.fromString(userId)).ifPresent(userRepository::delete);
    }

    public List<TenantData> getTenants() {
        return Stream.concat(
                predefinedTenants.values().stream(),
                tenantRepository.findAll().stream()
        )
                .map(Tenant::toData)
                .collect(Collectors.toList());
    }

}
