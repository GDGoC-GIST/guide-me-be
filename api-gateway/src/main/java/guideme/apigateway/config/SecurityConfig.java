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
import org.springframework.security.config.web.server.ServerHttpSecurity.CorsSpec;
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

        return http
                .cors(CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable).authorizeExchange(
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
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 모든 도메인 허용 (Spring 2.4+는 addAllowedOrigin 대신 addAllowedOriginPattern 사용)
        config.addAllowedMethod("*");        // GET, POST, OPTIONS 등
        config.addAllowedHeader("*");        // 모든 헤더 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 CORS 설정 적용
        return new CorsWebFilter(source);
    }

}
