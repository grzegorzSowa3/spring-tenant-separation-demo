package pl.recompiled.springtenantseparationdemo.security.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.recompiled.springtenantseparationdemo.security.user.dto.CreateUserDto;
import pl.recompiled.springtenantseparationdemo.security.user.dto.UserData;

import java.util.Set;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserData createUser(CreateUserDto dto) {
        return createUserWithAuthorities(dto, Set.of(Authority.USER));
    }

    public void deleteUser(String userId) {
        userRepository.findById(UUID.fromString(userId)).ifPresent(userRepository::delete);
    }

    public void createAdmin(CreateUserDto dto) {
        createUserWithAuthorities(dto, Set.of(Authority.ADMIN));
    }

    private UserData createUserWithAuthorities(CreateUserDto dto, Set<Authority> authorities) {
        final User user = User.newInstance(
                dto.getUsername(),
                passwordEncoder.encode(dto.getPassword()),
                authorities);
        return userRepository.save(user).toData();
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findOne(User.byUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("No user with such username!"));
    }
}
