package pl.recompiled.springtenantseparationdemo.task;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.recompiled.springtenantseparationdemo.security.TypeAwareRepository;

@Configuration
public class TaskConfiguration {

    @Bean
    public TypeAwareRepository<Task> typeAwareTaskRepository(TaskRepository userRepository) {
        return new TypeAwareRepository<>(userRepository, Task.class);
    }
}
