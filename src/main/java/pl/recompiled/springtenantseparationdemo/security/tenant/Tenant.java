package pl.recompiled.springtenantseparationdemo.security.tenant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;
import pl.recompiled.springtenantseparationdemo.security.user.dto.TenantData;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Tenant implements Persistable<TenantId> {

    @Id
    @Embedded
    private TenantId id;
    @Transient
    private boolean isNew;
    private String name;

    public static Tenant newInstance(String name) {
        final Tenant tenant = of(TenantId.random(), name);
        tenant.isNew = true;
        return tenant;
    }

    static Tenant of(TenantId id, String name) {
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
