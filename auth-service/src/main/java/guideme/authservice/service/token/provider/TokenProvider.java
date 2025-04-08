package guideme.authservice.service.token.provider;

import guideme.authservice.domain.token.value.TokenType;
import guideme.authservice.service.token.generator.JWTEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenProvider {

    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 900000;
    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 432000000;
    private final JWTEncoder jwtEncoder;

    public String generateToken(TokenType tokenType, String userId, String role) {
        if (tokenType.equals(TokenType.ACCESS)) {
            return generateJWT(ACCESS_TOKEN_VALIDITY_SECONDS, userId, role);
        }
        return generateJWT(REFRESH_TOKEN_VALIDITY_SECONDS, userId, role);
    }

    private String generateJWT(long expireTerm, String userId, String role) {
        return jwtEncoder.createToken(userId, role, expireTerm);
    }
}
