package guideme.authservice.service.token.generator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JWTDecoder {

    @Value("${jwt.secret}")
    private final String secret;
    @Value("${jwt.issuer}")
    private final String issuer;

    public DecodedJWT decode(String tokenValue) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();

        try {
            return verifier.verify(tokenValue);
        } catch (JWTVerificationException e) {
            throw new RuntimeException(e);
        }
    }
}
