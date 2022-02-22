package pl.recompiled.springtenantseparationdemo.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.recompiled.springtenantseparationdemo.security.TenantAware;
import pl.recompiled.springtenantseparationdemo.security.user.dto.CreateUserDto;
import pl.recompiled.springtenantseparationdemo.security.user.dto.UserData;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
class UserEndpoint {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<UserData> createUser(@RequestBody CreateUserDto dto) {
        final UserData user = userService.createUser(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @DeleteMapping("{userId}")
    @PreAuthorize("@sameTenantValidator.validate(#userId, 'pl.recompiled.springtenantseparationdemo.security.user.User')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @TenantAware
    public Map<String, List<UserData>> getUsers() {
        return Collections.singletonMap("users",
                userRepository.findAll().stream()
                        .map(User::toData)
                        .collect(Collectors.toList()));
    }
}
