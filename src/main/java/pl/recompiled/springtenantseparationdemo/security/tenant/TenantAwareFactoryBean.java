package pl.recompiled.springtenantseparationdemo.security.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class TenantAwareFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable> extends JpaRepositoryFactoryBean<R, T, I> {

    public TenantAwareFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new TenantAwareJpaExecutorFactory(entityManager);
    }

    private static class TenantAwareJpaExecutorFactory<T, I extends Serializable> extends JpaRepositoryFactory {

        public TenantAwareJpaExecutorFactory(EntityManager entityManager) {
            super(entityManager);
        }

        @Override
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
            if (TenantAdherentEntity.class.isAssignableFrom(information.getDomainType())) {
                return new TenantAwareJpaRepository(super.getEntityInformation(information.getDomainType()), entityManager);
            } else {
                return super.getTargetRepository(information, entityManager);
            }
        }

        @Override
        protected Class getRepositoryBaseClass(RepositoryMetadata metadata) {
            if (TenantAdherentEntity.class.isAssignableFrom(metadata.getDomainType())) {
                return TenantAwareJpaRepository.class;
            } else {
                return SimpleJpaRepository.class;
            }
        }
    }
}
