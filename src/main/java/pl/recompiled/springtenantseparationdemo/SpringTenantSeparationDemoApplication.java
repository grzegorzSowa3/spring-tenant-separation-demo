package pl.recompiled.springtenantseparationdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.recompiled.springtenantseparationdemo.security.tenant.TenantAwareJpaRepository;


@Configuration
@EnableJpaRepositories(repositoryBaseClass = TenantAwareJpaRepository.class)
@SpringBootApplication
public class SpringTenantSeparationDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringTenantSeparationDemoApplication.class, args);
    }

}
