package pl.recompiled.springtenantseparationdemo.security;

import org.springframework.stereotype.Component;
import pl.recompiled.springtenantseparationdemo.security.user.TenantAdherent;
import pl.recompiled.springtenantseparationdemo.security.user.TenantContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class SameTenantChecker {

    private final Map<Class<?>, TypeAwareRepository<?>> repositories;

    public SameTenantChecker(List<TypeAwareRepository<?>> repositories) {
        this.repositories = new HashMap<>();
        for (TypeAwareRepository<?> repository : repositories) {
            this.repositories.put(repository.handles(), repository);
        }
    }

    public boolean check(UUID id, String className) {
        Class<?> type = parseClassName(className);
        final Optional<TenantAdherent> tenantAdherent = repositoryForType(type)
                .findById(id)
                .map(TenantAdherent.class::cast);
        boolean result = false;
        if (tenantAdherent.isPresent()) {
            result = tenantAdherent.get().getTenantId().equals(TenantContext.getTenantId());
        }
        return result;
    }

    private Class<?> parseClassName(String className) {
        Class<?> type;
        try {
            type = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No class with name: " + className, e);
        }
        return type;
    }

    private TypeAwareRepository<?> repositoryForType(Class<?> type) {
        final TypeAwareRepository<?> repository = repositories.get(type);
        if (repository == null) {
            throw new RuntimeException("No TypeAwareRepository for type: " + type);
        }
        return repository;
    }
}
