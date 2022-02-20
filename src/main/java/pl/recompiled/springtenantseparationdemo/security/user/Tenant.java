package pl.recompiled.springtenantseparationdemo.security.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;
import pl.recompiled.springtenantseparationdemo.security.user.dto.TenantData;
import pl.recompiled.springtenantseparationdemo.security.user.dto.UserData;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Tenant implements Persistable<UUID> {

    @Id
    private UUID id;
    @Transient
    private boolean isNew;
    private String name;

    public static Tenant newInstance(String name) {
        final Tenant tenant = of(UUID.randomUUID(), name);
        tenant.isNew = true;
        return tenant;
    }

    static Tenant of(UUID id, String name) {
        final Tenant tenant = new Tenant();
        tenant.id = id;
        tenant.name = name;
        return tenant;
    }

    TenantData toData() {
        final TenantData data = new TenantData();
        data.setId(id.toString());
        data.setName(name);
        return data;
    }
}
