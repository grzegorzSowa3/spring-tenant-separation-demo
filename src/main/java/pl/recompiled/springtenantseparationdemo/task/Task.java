package pl.recompiled.springtenantseparationdemo.task;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;
import pl.recompiled.springtenantseparationdemo.security.user.TenantAdherent;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task implements TenantAdherent, Persistable<UUID> {

    @Id
    private UUID id;
    @Transient
    private boolean isNew;
    private String tenantId;
    private String title;

    public static Task newInstance(String tenantId, String title) {
        final Task task = new Task();
        task.id = UUID.randomUUID();
        task.isNew = true;
        task.tenantId = tenantId;
        task.title = title;
        return task;
    }
}
