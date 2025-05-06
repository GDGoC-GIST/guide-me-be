package guideme.apigateway.filter;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.autoconfigure.web.ServerProperties.Reactive;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class UserIdInjectFilter implements WebFilter {

    private static final String X_USER_ID = "X-User-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth != null && auth.isAuthenticated())
                .flatMap(auth -> {
                    String userId = String.valueOf(auth.getPrincipal());
                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .headers(h -> h.remove(X_USER_ID))
                            .header(X_USER_ID, userId)
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .switchIfEmpty(chain.filter(exchange))     // 🔑 no authentication → just continue
                .onErrorResume(e -> chain.filter(exchange)); // 🔑 any error → still continue
    }
}
