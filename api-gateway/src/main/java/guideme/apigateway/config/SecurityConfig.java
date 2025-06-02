package guideme.apigateway.config;

import guideme.apigateway.filter.BearerTokenConverter;
import guideme.apigateway.filter.CustomAuthenticationManager;
import guideme.apigateway.filter.UserIdInjectFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.RequestPath;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final List<String> WHITE_LIST = List.of("/api/auth/**", "/api/user/v3/api-docs/**",
            "/api/user/swagger-ui/**", "/actuator/health");
    private final CustomAuthenticationManager authManager;
    private final PathPatternParser parser = new PathPatternParser();

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, UserIdInjectFilter userIdInjectFilter) {

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authManager);
        authenticationWebFilter.setServerAuthenticationConverter(new BearerTokenConverter());
        authenticationWebFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        return http.csrf(ServerHttpSecurity.CsrfSpec::disable).authorizeExchange(
                        exchanges -> exchanges
                                .pathMatchers("/actuator/health/**").permitAll()
                                .pathMatchers("/api/auth/**").permitAll()
                                .pathMatchers("/api/user/swagger-ui/**").permitAll()
                                .pathMatchers("/api/user/v3/api-docs/**").permitAll()
                                .anyExchange().authenticated())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(FormLoginSpec::disable)
                .addFilterAt((exchange, chain) -> {
                    RequestPath path = exchange.getRequest().getPath();
                    if (WHITE_LIST.stream().anyMatch(p -> parser.parse(p).matches(path))) {
                        log.info(path.value());
                        return chain.filter(exchange); // 인증 필터 스킵
                    }
                    return authenticationWebFilter.filter(exchange, chain);
                }, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAfter(userIdInjectFilter, SecurityWebFiltersOrder.AUTHENTICATION).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    @Bean
    public CorsWebFilter corsWebFilter(CorsConfigurationSource source) {
        return new CorsWebFilter(source);
    }

}
