package guideme.apigateway.filter;

import guideme.apigateway.client.AuthServiceClient;
import guideme.apigateway.dto.AuthVerificationResponse;
import guideme.apigateway.dto.GlobalResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {

    private final AuthServiceClient authServiceClient;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        try {
            GlobalResponse<AuthVerificationResponse> result = authServiceClient.verify("Bearer " + token);
            if (!result.success()) {
                throw new IllegalArgumentException("token검증 실패");
            }
            AuthVerificationResponse res = result.data();
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(res.role()));
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    res.userId(), null, authorities);
            return Mono.just(auth);
        } catch (Exception e) {
            return Mono.error(new BadCredentialsException("Invalid token"));
        }
    }
}
