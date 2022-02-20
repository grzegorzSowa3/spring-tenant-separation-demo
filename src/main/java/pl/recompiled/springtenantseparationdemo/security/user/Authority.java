package pl.recompiled.springtenantseparationdemo.security.user;

import org.springframework.security.core.GrantedAuthority;

enum Authority implements GrantedAuthority {
    USER, ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
