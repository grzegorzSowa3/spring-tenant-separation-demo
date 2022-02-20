package pl.recompiled.springtenantseparationdemo.security;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;
import org.springframework.data.domain.Persistable;
import pl.recompiled.springtenantseparationdemo.security.user.TenantAdherent;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.UUID;

@MappedSuperclass
@FilterDef(name = "tenant_aware_filter", parameters = {@ParamDef(name = "tenant_id", type = "string")})
@Filters(@Filter(name = "tenant_aware_filter", condition = "tenant_id=:tenant_id"))
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class TenantAdherentEntity implements TenantAdherent, Persistable<UUID> {

    @Id
    private UUID id;
    @Transient
    private boolean isNew;
    private String tenantId;

}
