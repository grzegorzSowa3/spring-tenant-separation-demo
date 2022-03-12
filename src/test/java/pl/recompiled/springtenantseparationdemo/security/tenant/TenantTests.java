package pl.recompiled.springtenantseparationdemo.security.tenant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pl.recompiled.springtenantseparationdemo.security.user.dto.CreateTenantDto;
import pl.recompiled.springtenantseparationdemo.security.user.dto.CreateUserDto;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class TenantTests {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public TenantTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void canCreateTenant() throws Exception {

        //when: tenant is created
        CreateTenantDto tenant = newTenant();
        ResultActions result = mockMvc.perform(createTenantRequest(tenant));

        //then: there is CREATED response
        result.andExpect(status().isCreated());

        //and: admin can log in
        login(tenant.getAdmin()).andExpect(status().isOk());
    }

    private MockHttpServletRequestBuilder createTenantRequest(CreateTenantDto tenant) throws JsonProcessingException {
        return post("/tenants")
                .content(objectMapper.writeValueAsString(tenant))
                .contentType(MediaType.APPLICATION_JSON);
    }

    private CreateTenantDto newTenant() {
        String tenant = "new-tenant-" + UUID.randomUUID();
        return new CreateTenantDto(tenant,
                new CreateUserDto(tenant + "-admin", "pass"));
    }

    private ResultActions login(CreateUserDto dto) throws Exception {
        return mockMvc.perform(post("/login")
                .param("username", dto.getUsername())
                .param("password", dto.getPassword())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.ALL));
    }
}
