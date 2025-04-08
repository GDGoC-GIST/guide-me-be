package guideme.authservice.infrastructure.dto;

import guideme.authservice.domain.token.value.TokenType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TokenReadRequest {
    private String value;
    private String userId;
    private String role;
    private long issuedAt;
    private long currentTime;
    private long expiresAt;
    private String issuer;
    private TokenType tokenType;
}
