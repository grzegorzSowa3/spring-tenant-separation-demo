package pl.recompiled.springtenantseparationdemo.security.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;
import org.springframework.security.core.userdetails.UserDetails;
import pl.recompiled.springtenantseparationdemo.security.user.dto.UserData;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "app_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class User implements UserDetails, TenantAdherent, Persistable<UUID> {

    @Id
    private UUID id;
    @Transient
    private boolean isNew;
    private String tenantId;
    @Column(unique = true)
    private String username;
    private String password;

    @Convert(converter = AuthoritiesToStringConverter.class)
    private Set<Authority> authorities;

    public static User newInstance(String tenantId,
                                   String username,
                                   String password,
                                   Set<Authority> authorities) {
        final User user = new User();
        user.id = UUID.randomUUID();
        user.isNew = true;
        user.tenantId = tenantId;
        user.username = username;
        user.password = password;
        user.authorities = authorities;
        return user;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserData toData() {
        final UserData data = new UserData();
        data.setId(id.toString());
        data.setUsername(username);
        return data;
    }
}
