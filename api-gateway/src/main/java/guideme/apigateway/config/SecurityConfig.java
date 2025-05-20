package guideme.apigateway.config;

import guideme.apigateway.filter.BearerTokenConverter;
import guideme.apigateway.filter.CustomAuthenticationManager;
import guideme.apigateway.filter.UserIdInjectFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationManager authManager;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, UserIdInjectFilter userIdInjectFilter) {

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authManager);
        authenticationWebFilter.setServerAuthenticationConverter(new BearerTokenConverter());
        authenticationWebFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        http.addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.csrf(ServerHttpSecurity.CsrfSpec::disable).authorizeExchange(
                        exchange -> exchange.pathMatchers("/actuator/health/**").permitAll().pathMatchers("/api/auth/**")
                                .permitAll().pathMatchers("/api/user/v3/api-docs").permitAll()
                                .pathMatchers("/api/user/swagger-ui.index.html").permitAll()
                                .pathMatchers("/api/user/v3/api-docs/swagger-config").permitAll().anyExchange().authenticated())
                .formLogin(FormLoginSpec::disable)
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAfter(userIdInjectFilter, SecurityWebFiltersOrder.AUTHENTICATION).build();
    }
}
