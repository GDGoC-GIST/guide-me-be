package guideme.apigateway.config;

import guideme.apigateway.filter.CustomAuthenticationManager;
import guideme.apigateway.filter.UserIdInjectFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationManager authManager;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, UserIdInjectFilter userIdInjectFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/health/**").permitAll()
                        .pathMatchers("/api/auth/**","/api/auth/**").permitAll()
                        .pathMatchers("/admin/**").hasRole("ADMIN")
                        .pathMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .anyExchange().authenticated()
                )
                .authenticationManager(authManager)
                .addFilterAfter(userIdInjectFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
