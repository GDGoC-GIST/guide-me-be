package guideme.apigateway.filter;

import guideme.apigateway.client.AuthServiceClient;
import guideme.apigateway.dto.AuthVerificationResponse;
import guideme.apigateway.dto.GlobalResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {

    private final AuthServiceClient authServiceClient;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        log.info("auth request {}", token);
        return authServiceClient.verify("Bearer " + token)
                .flatMap(result -> {
                    if (!result.success()) {
                        return Mono.error(new BadCredentialsException("Token 검증 실패"));
                    }
                    AuthVerificationResponse res = result.data();
                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(res.role()));
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            res.userId(), null, authorities);
                    return Mono.just(auth);
                })
                .onErrorMap(e -> new BadCredentialsException("Invalid token", e));
    }
}
