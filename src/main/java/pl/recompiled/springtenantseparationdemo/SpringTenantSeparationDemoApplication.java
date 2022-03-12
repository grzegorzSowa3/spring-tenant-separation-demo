package pl.recompiled.springtenantseparationdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import pl.recompiled.springtenantseparationdemo.security.tenant.TenantAwareFactoryBean;


@EnableJpaRepositories(repositoryFactoryBeanClass = TenantAwareFactoryBean.class)
@EnableMethodSecurity
@SpringBootApplication
public class SpringTenantSeparationDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringTenantSeparationDemoApplication.class, args);
    }

}
