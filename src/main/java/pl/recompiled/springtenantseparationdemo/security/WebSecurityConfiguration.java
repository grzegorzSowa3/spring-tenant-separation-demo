package pl.recompiled.springtenantseparationdemo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.recompiled.springtenantseparationdemo.security.user.PredefinedUsers;
import pl.recompiled.springtenantseparationdemo.security.user.UserService;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final PredefinedUsers predefinedUsers;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/tenants").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler((req, resp, auth) -> resp.setStatus(OK.value()))
                .failureHandler((req, resp, auth) -> resp.setStatus(UNAUTHORIZED.value()))
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((req, resp, ex) -> resp.setStatus(UNAUTHORIZED.value()))
                .and()
                .logout()
                .and()
                .csrf().disable();
    }

}
