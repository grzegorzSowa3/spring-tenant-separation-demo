package pl.recompiled.springtenantseparationdemo.security.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.recompiled.springtenantseparationdemo.security.TypeAwareRepository;

@Configuration
public class UserConfiguration {

    @Bean
    public TypeAwareRepository<User> typeAwareUserRepository(UserRepository userRepository) {
        return new TypeAwareRepository<>(userRepository, User.class);
    }
}
