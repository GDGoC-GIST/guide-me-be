package guideme.apigateway.config;

import guideme.apigateway.filter.BearerTokenConverter;
import guideme.apigateway.filter.CustomAuthenticationManager;
import guideme.apigateway.filter.UserIdInjectFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationManager authManager;

    private static final List<String> WHITE_LIST = List.of(
            "/api/auth/",
            "/api/public/",
            "/api/user/v3/api-docs",
            "/api/user/swagger-ui/index.html",
            "/api/user/v3/api-docs/swagger-config",
            "/actuator/health"
    );

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, UserIdInjectFilter userIdInjectFilter) {

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authManager);
        authenticationWebFilter.setServerAuthenticationConverter(new BearerTokenConverter());
        authenticationWebFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges.pathMatchers("/actuator/health/**").permitAll().pathMatchers("/api/auth/**")
                        .permitAll().pathMatchers("/api/user/v3/api-docs").permitAll()
                        .pathMatchers("/api/user/swagger-ui/index.html").permitAll()
                        .pathMatchers("/api/user/v3/api-docs/swagger-config").permitAll()
                .anyExchange().authenticated())
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .addFilterAt((exchange, chain) -> {
                    String path = exchange.getRequest().getPath().value();
                    log.info(path);
                    if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
                        return chain.filter(exchange); // 인증 필터 스킵
                    }
                    return authenticationWebFilter.filter(exchange, chain);
                }, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAfter(userIdInjectFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
