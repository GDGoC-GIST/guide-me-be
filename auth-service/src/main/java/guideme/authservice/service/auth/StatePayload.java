package guideme.authservice.service.auth;

import java.time.Instant;

public record StatePayload(String nonce, String codeVerifier, String redirectUri, Instant createdAt) {
}
