package pl.recompiled.springtenantseparationdemo.security.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTenantDto {

    private String name;
    private CreateUserDto admin;

}
