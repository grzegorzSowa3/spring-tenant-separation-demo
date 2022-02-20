package pl.recompiled.springtenantseparationdemo.task;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.recompiled.springtenantseparationdemo.security.user.TenantContext;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskEndpoint {

    private final TaskRepository repository;

    @PostMapping
    public ResponseEntity<Void> createTask(@RequestBody CreateTaskDto dto) {
        repository.save(Task.newInstance(TenantContext.getTenantId(), dto.getTitle()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        repository.deleteById(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<String> getAllTasks() {
        return repository.findAll().stream().map(Task::getTitle).collect(Collectors.toList());
    }
}

@Data
class CreateTaskDto {
    private String title;
}
