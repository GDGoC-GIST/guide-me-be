package guideme.authservice.domain.token.model;

import guideme.authservice.domain.token.value.TokenType;
import guideme.authservice.infrastructure.dto.TokenReadRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Token {
    private final String userId;
    private final String role;
    private final long issuedAt;
    private final long currentTime;
    private final long expiresAt;
    private final String issuer;
    private final TokenType tokenType;

    @Builder(access = AccessLevel.PRIVATE)
    public Token(long expiresAt, String userId, String role, long issuedAt, long currentTime, String issuer,
                 TokenType tokenType) {
        this.expiresAt = expiresAt;
        this.userId = userId;
        this.role = role;
        this.issuedAt = issuedAt;
        this.currentTime = currentTime;
        this.issuer = issuer;
        this.tokenType = tokenType;
    }

    public static Token create(TokenReadRequest request) {
        return Token.builder().userId(request.getUserId()).role(request.getRole()).expiresAt(request.getExpiresAt())
                .issuedAt(request.getIssuedAt()).currentTime(request.getCurrentTime()).issuer(request.getIssuer())
                .tokenType(request.getTokenType()).build();
    }

    public boolean isExpired() {
        return currentTime > expiresAt;
    }
}
