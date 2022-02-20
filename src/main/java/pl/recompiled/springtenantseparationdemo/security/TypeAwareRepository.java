package pl.recompiled.springtenantseparationdemo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import pl.recompiled.springtenantseparationdemo.security.user.TenantAdherent;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class TypeAwareRepository<T extends TenantAdherent> implements CrudRepository<T, UUID> {

    private final CrudRepository<T, UUID> repository;
    private final Class<T> type;

    public Class<T> handles() {
        return type;
    }

    @Override
    public <S extends T> S save(S entity) {
        return repository.save(entity);
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public Optional<T> findById(UUID aUUID) {
        return repository.findById(aUUID);
    }

    @Override
    public boolean existsById(UUID aUUID) {
        return repository.existsById(aUUID);
    }

    @Override
    public Iterable<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Iterable<T> findAllById(Iterable<UUID> longs) {
        return repository.findAllById(longs);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void deleteById(UUID aUUID) {
        repository.deleteById(aUUID);
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> longs) {
        repository.deleteAllById(longs);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
