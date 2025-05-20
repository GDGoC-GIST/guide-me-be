package guideme.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UserIdInjectFilter implements WebFilter {

    private static final String X_USER_ID = "X-User-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getPrincipal().cast(Authentication.class).flatMap(auth -> {
            String userId = auth.getName();
            log.info("called {} ",userId);
            exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.add(X_USER_ID, userId)).build();
            return chain.filter(exchange);
        });
    }
}
