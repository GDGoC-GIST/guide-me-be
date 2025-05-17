package guideme.authservice.config;

import guideme.authservice.service.token.InternalApiAuthFilter;
import guideme.authservice.service.token.JwtAuthenticationFilter;
import guideme.authservice.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenService tokenService;

    @Bean
    public InternalApiAuthFilter internalApiAuthFilter() {
        return new InternalApiAuthFilter();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(TokenService tokenService) {
        return new JwtAuthenticationFilter(tokenService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//                .authorizeHttpRequests(
//                        authz -> authz.requestMatchers("/internal/**").authenticated().anyRequest().permitAll())
                .addFilterAfter(jwtAuthenticationFilter(tokenService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(internalApiAuthFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }
}