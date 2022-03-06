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
        if (isTenantAware()) {
            return repository.findAll(example());
        } else {
            return repository.findAll();
        }
    }

    @Override
    public List<T> findAll(Sort sort) {
        if (isTenantAware()) {
            return repository.findAll(example(), sort);
        } else {
            return repository.findAll(sort);
        }
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        if (isTenantAware()) {
            return repository.findAll(example(), pageable);
        } else {
            return repository.findAll(pageable);
        }
    }

    @Override
    public List<T> findAllById(Iterable<UUID> uuids) {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("find all by ids unsupported");
        } else {
            return repository.findAllById(uuids);
        }
    }

    @Override
    public long count() {
        if (isTenantAware()) {
            return repository.count(example());
        } else {
            return repository.count();
        }
    }

    @Override
    public void deleteById(UUID uuid) {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("delete by id unsupported");
        } else {
            repository.deleteById(uuid);
        }
    }

    @Override
    public void delete(T entity) {
        if (isTenantAware()) {
            if (entity.getTenantId().equals(TenantContext.getTenantId())) {
                repository.delete(entity);
            }
        } else {
            repository.delete(entity);
        }
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("delete by id unsupported");
        } else {
            repository.deleteAllById(uuids);
        }
    }

    @Override
    public void deleteAll() {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("delete all unsupported");
        } else {
            repository.deleteAll();
        }
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        if (isTenantAware()) {
            List<? extends T> tenantEntities = StreamSupport.stream(entities.spliterator(), false)
                    .filter(entity -> entity.getTenantId().equals(TenantContext.getTenantId()))
                    .collect(Collectors.toList());
            repository.deleteAll(tenantEntities);
        } else {
            repository.deleteAll(entities);
        }
    }

    @Override
    public <S extends T> S save(S entity) {
        if (isTenantAware()) {
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
                entity.setNew(true);
            }
            return repository.save(tenant(entity));
        } else {
            return repository.save(entity);
        }
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("save all unsupported");
        } else {
            return repository.saveAll(entities);
        }
    }

    @Override
    public Optional<T> findById(UUID uuid) {
        if (isTenantAware()) {
            return repository.findOne(Example.of(instance(uuid)));
        } else {
            return repository.findById(uuid);
        }
    }

    @Override
    public boolean existsById(UUID uuid) {
        if (isTenantAware()) {
            return exists(Example.of(instance(uuid)));
        } else {
            return repository.existsById(uuid);
        }
    }

    @Override
    public void flush() {
        repository.flush();
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        if (isTenantAware()) {
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
                entity.setNew(true);
            }
            return repository.saveAndFlush(tenant(entity));
        } else {
            return repository.saveAndFlush(entity);
        }
    }

    @Override
    public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("save all unsupported");
        } else {
            return repository.saveAllAndFlush(entities);
        }
    }

    @Override
    public void deleteAllInBatch(Iterable<T> entities) {
        if (isTenantAware()) {
            List<T> tenantEntities = StreamSupport.stream(entities.spliterator(), false)
                    .filter(entity -> entity.getTenantId().equals(TenantContext.getTenantId()))
                    .collect(Collectors.toList());
            repository.deleteAllInBatch(tenantEntities);
        } else {
            repository.deleteAllInBatch(entities);
        }
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("delete by id unsupported");
        } else {
            repository.deleteAllByIdInBatch(uuids);
        }
    }

    @Override
    public void deleteAllInBatch() {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("delete all unsupported");
        } else {
            repository.deleteAllInBatch();
        }
    }

    @Override
    public T getOne(UUID uuid) {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("get one unsupported");
        } else {
            return repository.getOne(uuid);
        }
    }

    @Override
    public T getById(UUID uuid) {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("get by id unsupported");
        } else {
            return repository.getById(uuid);
        }
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        if (isTenantAware()) {
            return repository.findOne(tenant(example));
        } else {
            return repository.findOne(example);
        }
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        if (isTenantAware()) {
            return repository.findAll(tenant(example));
        } else {
            return repository.findAll(example);
        }
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        if (isTenantAware()) {
            return repository.findAll(tenant(example));
        } else {
            return repository.findAll(example, sort);
        }
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        if (isTenantAware()) {
            return repository.findAll(tenant(example), pageable);
        } else {
            return repository.findAll(example, pageable);
        }
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        if (isTenantAware()) {
            return repository.count(tenant(example));
        } else {
            return repository.count(example);
        }
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        if (isTenantAware()) {
            return repository.exists(tenant(example));
        } else {
            return repository.exists(example);
        }
    }

    @Override
    public <S extends T, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        if (isTenantAware()) {
            return repository.findBy(tenant(example), queryFunction);
        } else {
            return repository.findBy(example, queryFunction);
        }
    }

    @Override
    public Optional<T> findOne(Specification<T> spec) {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("specifications unsupported");
        } else {
            return repository.findOne(spec);
        }
    }

    @Override
    public List<T> findAll(Specification<T> spec) {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("specifications unsupported");
        } else {
            return repository.findAll(spec);
        }
    }

    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("specifications unsupported");
        } else {
            return repository.findAll(spec, pageable);
        }
    }

    @Override
    public List<T> findAll(Specification<T> spec, Sort sort) {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("specifications unsupported");
        } else {
            return repository.findAll(spec, sort);
        }
    }

    @Override
    public long count(Specification<T> spec) {
        if (isTenantAware()) {
            throw new UnsupportedOperationException("specifications unsupported");
        } else {
            return repository.count(spec);
        }
    }

    @Override
    public void setRepositoryMethodMetadata(CrudMethodMetadata crudMethodMetadata) {
        repository.setRepositoryMethodMetadata(crudMethodMetadata);
    }

    private boolean isTenantAware() {
        return TenantAdherentEntity.class.isAssignableFrom(metadata.getJavaType());
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
