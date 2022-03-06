package pl.recompiled.springtenantseparationdemo.security.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.UserDetails;
import pl.recompiled.springtenantseparationdemo.security.tenant.TenantAdherentEntity;
import pl.recompiled.springtenantseparationdemo.security.user.dto.UserData;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "app_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class User extends TenantAdherentEntity implements UserDetails {

    @Column(unique = true)
    private String username;
    private String password;

    @Convert(converter = AuthoritiesToStringConverter.class)
    private Set<Authority> authorities;

    public static User newInstance(String username,
                                   String password,
                                   Set<Authority> authorities) {
        final User user = new User();
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
        data.setId(getId().toString());
        data.setUsername(username);
        return data;
    }

    static Example<User> byUsername(String username) {
        User user = new User();
        user.username = username;
        return Example.of(user);
    }
}
