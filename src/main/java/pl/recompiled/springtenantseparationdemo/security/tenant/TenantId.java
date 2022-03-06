package pl.recompiled.springtenantseparationdemo.security.tenant;


import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Embeddable
public class TenantId implements Serializable {

    private UUID value;

    public static TenantId any() {
        return new TenantId();
    }

    public static TenantId of(String value) {
        assert value != null;
        TenantId tenantId = new TenantId();
        tenantId.value = UUID.fromString(value);
        return tenantId;
    }

    public static TenantId random() {
        TenantId tenantId = new TenantId();
        tenantId.value = UUID.randomUUID();
        return tenantId;
    }

    @Override
    public String toString() {
        return Optional.of(value)
                .map(UUID::toString)
                .orElse("");
    }
}
