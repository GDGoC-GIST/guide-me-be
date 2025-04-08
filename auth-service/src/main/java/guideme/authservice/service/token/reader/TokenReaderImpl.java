package guideme.authservice.service.token.reader;

import com.auth0.jwt.interfaces.DecodedJWT;
import guideme.authservice.domain.token.model.Token;
import guideme.authservice.domain.token.value.TokenType;
import guideme.authservice.infrastructure.dto.TokenReadRequest;
import guideme.authservice.service.token.generator.JWTDecoder;
import guideme.authservice.util.clock.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenReaderImpl implements TokenReader {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_USER_ID = "userId";

    private final ClockHolder clockHolder;
    private final JWTDecoder jwtDecoder;

    public Token handle(String tokenValue) {
        DecodedJWT jwt = jwtDecoder.decode(tokenValue);
        TokenReadRequest readRequest = parseToken(jwt);
        return Token.create(readRequest);
    }

    private TokenReadRequest parseToken(DecodedJWT jwt) {
        TokenReadRequest readRequest = new TokenReadRequest();
        readRequest.setTokenType(TokenType.REFRESH);
        readRequest.setCurrentTime(clockHolder.now());
        readRequest.setRole(jwt.getClaim(CLAIM_ROLE).asString());
        readRequest.setUserId(jwt.getClaim(CLAIM_USER_ID).asString());
        readRequest.setIssuer(jwt.getIssuer());
        readRequest.setValue(jwt.getToken());
        readRequest.setExpiresAt(jwt.getExpiresAt().getTime());
        readRequest.setIssuedAt(jwt.getIssuedAt().getTime());
        return readRequest;
    }

}
