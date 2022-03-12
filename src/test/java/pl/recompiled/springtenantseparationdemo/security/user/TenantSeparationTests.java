package pl.recompiled.springtenantseparationdemo.security.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pl.recompiled.springtenantseparationdemo.security.tenant.PredefinedTenants;
import pl.recompiled.springtenantseparationdemo.security.tenant.PredefinedTenants.PredefinedTenant;
import pl.recompiled.springtenantseparationdemo.security.tenant.TenantAdherent;
import pl.recompiled.springtenantseparationdemo.security.user.dto.CreateUserDto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class TenantSeparationTests {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PredefinedTenant thisTenant;
    private final PredefinedTenant otherTenant;
    private final UserRepository userRepository;

    private final String userByUsername = "$.users[?(@.username == '%s')]";

    @Autowired
    public TenantSeparationTests(MockMvc mockMvc,
                                 PredefinedTenants properties,
                                 UserRepository userRepository) throws Exception {
        this.thisTenant = properties.getTenants().get(0);
        this.otherTenant = properties.getTenants().get(1);
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
    }

    @BeforeAll
    public void setup() throws Exception {
        createTenantUsers();
    }

    @Test
    public void adminCanCreateUser() throws Exception {

        //given: logged admin
        MockHttpSession admin = loginAdmin(thisTenant);

        //when: admin creates user
        CreateUserDto user = newUser();
        ResultActions result = mockMvc.perform(createUserRequest(user)
                .session(admin));

        //then: there is CREATED response
        result.andExpect(status().isCreated());

        //and: new user is created
        Optional<? extends TenantAdherent> createdUser = findUser(user.getUsername());
        assert createdUser.isPresent();

        //and: it belongs to the same tenant
        assert createdUser.get().getTenantId().equals(thisTenant.getId());

        //and: user can login
        login(user).andExpect(status().isOk());
    }

    @Test
    public void userCanNotCreateUser() throws Exception {

        //given: user under tenant
        CreateUserDto user = newUser();
        createUserForTenant(user, thisTenant);

        //and: user logged in
        MockHttpSession userSession = (MockHttpSession) login(user)
                .andReturn().getRequest().getSession();

        //when: tries to create user
        CreateUserDto newUser = newUser();
        ResultActions result = mockMvc.perform(createUserRequest(newUser)
                .session(userSession));

        //then: there is FORBIDDEN response
        result.andExpect(status().isForbidden());

        //and: new user is not created
        Optional<? extends TenantAdherent> createdUser = findUser(newUser.getUsername());
        assert createdUser.isEmpty();
    }

    @Test
    public void adminCanQueryUsersFromTheSameTenantOnly() throws Exception {

        //given: logged admin
        MockHttpSession admin = loginAdmin(thisTenant);

        //when: admin queries users
        ResultActions result = mockMvc.perform(
                get("/users")
                        .session(admin));

        //then: response is OK
        result.andExpect(status().isOk());

        //and: admin gets users from his tenant
        result.andExpect(jsonPath(userByUsername, testUsers1().get(0).getUsername()).exists());
        result.andExpect(jsonPath(userByUsername, testUsers1().get(1).getUsername()).exists());

        //and: admin gets no users from other tenant
        result.andExpect(jsonPath(userByUsername, testUsers2().get(0).getUsername()).doesNotExist());
        result.andExpect(jsonPath(userByUsername, testUsers2().get(1).getUsername()).doesNotExist());

    }

    @Test
    public void adminCanDeleteUserFromTheSameTenant() throws Exception {

        //given: logged admin
        MockHttpSession admin = loginAdmin(thisTenant);

        //and: user to delete
        CreateUserDto targetUser = testUsers1().get(0);
        UUID targetUserId = findUser(targetUser.getUsername()).get().getId();

        //when: admin attempts to delete user from the same tenant
        ResultActions result = mockMvc.perform(
                delete("/users/{userId}", targetUserId)
                        .session(admin));

        //then: admin gets response no content
        result.andExpect(status().isNoContent());

        //and: user is deleted
        Optional<?> deletedUser = userRepository.findOne(User.byUsername(targetUser.getUsername()));
        assert deletedUser.isEmpty();

    }

    @Test
    public void adminCannotDeleteUserFromAnotherTenant() throws Exception {

        //given: logged admin
        MockHttpSession admin = loginAdmin(thisTenant);

        //and: user to delete from other tenant
        CreateUserDto targetUser = testUsers2().get(0);
        UUID targetUserId = findUser(targetUser.getUsername()).get().getId();

        //when: admin attempts to delete user from another tenant
        ResultActions result = mockMvc.perform(
                delete("/users/{userId}", targetUserId)
                        .session(admin));

        //then: admin gets response no content
        result.andExpect(status().isNoContent());

        //and: user is not deleted
        Optional<?> user = userRepository.findOne(User.byUsername(targetUser.getUsername()));
        assert user.isPresent();

    }

    private void createUserForTenant(CreateUserDto dto, PredefinedTenant tenant) throws Exception {
        MockHttpSession admin = loginAdmin(tenant);
        mockMvc.perform(createUserRequest(dto)
                .session(admin));
    }

    private Optional<User> findUser(String username) {
        return userRepository.findOne(User.byUsername(username));
    }


    private MockHttpServletRequestBuilder createUserRequest(CreateUserDto user) throws JsonProcessingException {
        return post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON);
    }

    private CreateUserDto newUser() {
        return new CreateUserDto("new-user-" + UUID.randomUUID(), "pass");
    }

    private void createTenantUsers() throws Exception {
        createUsers(thisTenant, testUsers1());
        createUsers(otherTenant, testUsers2());
    }

    private void createUsers(PredefinedTenant tenant, List<CreateUserDto> users) throws Exception {
        final MockHttpSession admin = loginAdmin(tenant);
        for (CreateUserDto user : users) {
            mockMvc.perform(createUserRequest(user)
                    .session(admin));
        }
    }

    private List<CreateUserDto> testUsers1() {
        return Arrays.asList(
                new CreateUserDto("private-user1", "pass"),
                new CreateUserDto("private-user2", "pass"));
    }

    private List<CreateUserDto> testUsers2() {
        return Arrays.asList(
                new CreateUserDto("vision-user1", "pass"),
                new CreateUserDto("vision-user2", "pass"));
    }

    private MockHttpSession loginAdmin(PredefinedTenant tenant) throws Exception {
        return (MockHttpSession) login(tenant.getAdminUser())
                .andExpect(status().isOk())
                .andReturn().getRequest().getSession();
    }

    private ResultActions login(CreateUserDto dto) throws Exception {
        return mockMvc.perform(post("/login")
                .param("username", dto.getUsername())
                .param("password", dto.getPassword())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.ALL));
    }
}
