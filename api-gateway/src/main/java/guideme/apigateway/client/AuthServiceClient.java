package guideme.apigateway.client;

import guideme.apigateway.dto.AuthVerificationResponse;
import guideme.apigateway.dto.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

//@FeignClient(name = "auth-service",url = "${client.auth-service.url}")
@Component
@RequiredArgsConstructor
public class AuthServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${client.auth-service.url}")
    private String authURL;

    public Mono<GlobalResponse<AuthVerificationResponse>> verify(String token) {
        return webClientBuilder.build()
                .get()
                .uri(authURL + "/api/auth/verify")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}
