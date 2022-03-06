package pl.recompiled.springtenantseparationdemo.security.tenant;


import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
public abstract class TenantAdherentEntity implements TenantAdherent, Persistable<UUID> {

    @Id
    private UUID id;
    @Transient
    private boolean isNew;
    @Embedded
    private TenantId tenantId;
}
