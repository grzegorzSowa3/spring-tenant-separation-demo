package pl.recompiled.springtenantseparationdemo.task;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.recompiled.springtenantseparationdemo.security.tenant.TenantAdherentEntity;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task extends TenantAdherentEntity {

    private String title;

    public static Task newInstance(String title) {
        final Task task = new Task();
        task.title = title;
        return task;
    }
}
