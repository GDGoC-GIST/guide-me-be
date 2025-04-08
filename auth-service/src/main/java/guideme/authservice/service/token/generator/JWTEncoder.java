package guideme.authservice.service.token.generator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import guideme.authservice.util.clock.ClockHolder;
import guideme.authservice.util.clock.UnixClockHolder;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTEncoder {
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_USER_ROLE = "role";

    private final Algorithm algorithm;
    private final String issuer;
    private final ClockHolder clockHolder;

    public JWTEncoder(@Value("${jwt.secret}") String secret, @Value("${jwt.issuer}") String issuer) {
        this.algorithm = Algorithm.HMAC256(secret);
        clockHolder = new UnixClockHolder();
        this.issuer = issuer;
    }

    public String createToken(String userId, String role, long expireTerm) {
        return JWT.create().withIssuer(issuer).withIssuedAt(Instant.ofEpochSecond(clockHolder.now()))
                .withClaim(CLAIM_USER_ID, userId).withClaim(CLAIM_USER_ROLE, role)
                .withExpiresAt(Instant.ofEpochSecond(clockHolder.now() + expireTerm)).sign(algorithm);
    }
}
