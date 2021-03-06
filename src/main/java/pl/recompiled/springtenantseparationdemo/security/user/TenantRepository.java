package pl.recompiled.springtenantseparationdemo.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface TenantRepository extends JpaRepository<Tenant, UUID> {

}
