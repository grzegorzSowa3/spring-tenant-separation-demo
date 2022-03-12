package pl.recompiled.springtenantseparationdemo.security.tenant;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Transactional
public class TenantAwareJpaRepository<T extends TenantAdherentEntity>
        implements JpaRepositoryImplementation<T, UUID> {

    private final JpaEntityInformation<T, UUID> metadata;
    private final SimpleJpaRepository<T, UUID> repository;

    public TenantAwareJpaRepository(JpaEntityInformation<T, UUID> entityInformation, EntityManager entityManager) {
        this.repository = new SimpleJpaRepository<>(entityInformation, entityManager);
        this.metadata = entityInformation;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll(example());
    }

    @Override
    public List<T> findAll(Sort sort) {
        return repository.findAll(example(), sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(example(), pageable);
    }

    @Override
    public List<T> findAllById(Iterable<UUID> uuids) {
        throw new UnsupportedOperationException("find all by ids unsupported");
    }

    @Override
    public long count() {
        return repository.count(example());
    }

    @Override
    public void deleteById(UUID uuid) {
        throw new UnsupportedOperationException("delete by id unsupported");
    }

    @Override
    public void delete(T entity) {
        if (entity.getTenantId().equals(TenantContext.getTenantId())) {
            repository.delete(entity);
        }
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {
        throw new UnsupportedOperationException("delete by id unsupported");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("delete all unsupported");
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        List<? extends T> tenantEntities = StreamSupport.stream(entities.spliterator(), false)
                .filter(entity -> entity.getTenantId().equals(TenantContext.getTenantId()))
                .collect(Collectors.toList());
        repository.deleteAll(tenantEntities);
    }

    @Override
    public <S extends T> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
            entity.setNew(true);
        }
        return repository.save(tenant(entity));
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("save all unsupported");
    }

    @Override
    public Optional<T> findById(UUID uuid) {
        return repository.findOne(Example.of(instance(uuid)));
    }

    @Override
    public boolean existsById(UUID uuid) {
        return exists(Example.of(instance(uuid)));
    }

    @Override
    public void flush() {
        repository.flush();
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
            entity.setNew(true);
        }
        return repository.saveAndFlush(tenant(entity));
    }

    @Override
    public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
        throw new UnsupportedOperationException("save all unsupported");
    }

    @Override
    public void deleteAllInBatch(Iterable<T> entities) {
        List<T> tenantEntities = StreamSupport.stream(entities.spliterator(), false)
                .filter(entity -> entity.getTenantId().equals(TenantContext.getTenantId()))
                .collect(Collectors.toList());
        repository.deleteAllInBatch(tenantEntities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {
        throw new UnsupportedOperationException("delete by id unsupported");
    }

    @Override
    public void deleteAllInBatch() {
        throw new UnsupportedOperationException("delete all unsupported");
    }

    @Override
    public T getOne(UUID uuid) {
        throw new UnsupportedOperationException("get one unsupported");
    }

    @Override
    public T getById(UUID uuid) {
        throw new UnsupportedOperationException("get by id unsupported");
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        return repository.findOne(tenant(example));
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return repository.findAll(tenant(example));
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return repository.findAll(tenant(example));
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return repository.findAll(tenant(example), pageable);
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        return repository.count(tenant(example));
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return repository.exists(tenant(example));
    }

    @Override
    public <S extends T, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return repository.findBy(tenant(example), queryFunction);
    }

    @Override
    public Optional<T> findOne(Specification<T> spec) {
        throw new UnsupportedOperationException("specifications unsupported");
    }

    @Override
    public List<T> findAll(Specification<T> spec) {
        throw new UnsupportedOperationException("specifications unsupported");
    }

    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        throw new UnsupportedOperationException("specifications unsupported");
    }

    @Override
    public List<T> findAll(Specification<T> spec, Sort sort) {
        throw new UnsupportedOperationException("specifications unsupported");
    }

    @Override
    public long count(Specification<T> spec) {
        throw new UnsupportedOperationException("specifications unsupported");
    }

    @Override
    public void setRepositoryMethodMetadata(CrudMethodMetadata crudMethodMetadata) {
        repository.setRepositoryMethodMetadata(crudMethodMetadata);
    }

    private Example<T> example() {
        return Example.of(instance());
    }

    private T instance() {
        try {
            Constructor<T> constructor = this.metadata.getJavaType().getDeclaredConstructor();
            constructor.setAccessible(true);
            return tenant(constructor.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private T instance(UUID id) {
        T instance = instance();
        instance.setId(id);
        return instance;
    }

    private <S extends T> Example<S> tenant(Example<S> example) {
        S entity = example.getProbe();
        return Example.of(tenant(entity));
    }

    private <S extends T> S tenant(S entity) {
        if (entity.getTenantId() == null) {
            entity.setTenantId(TenantContext.getTenantId());
        }
        return entity;
    }
}
