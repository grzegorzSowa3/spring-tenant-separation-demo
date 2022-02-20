package pl.recompiled.springtenantseparationdemo.task;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;
import pl.recompiled.springtenantseparationdemo.security.TenantAdherentEntity;
import pl.recompiled.springtenantseparationdemo.security.user.TenantAdherent;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task extends TenantAdherentEntity {

    private String title;

    public static Task newInstance(String tenantId, String title) {
        final Task task = new Task(tenantId);
        task.title = title;
        return task;
    }

    private Task(String tenantId) {
        super(UUID.randomUUID(), true, tenantId);
    }
}
