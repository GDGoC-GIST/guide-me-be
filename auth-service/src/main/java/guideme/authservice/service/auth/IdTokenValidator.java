package guideme.authservice.service.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdTokenValidator {
    private final OidcProperties prop;

    public JWTClaimsSet validate(String idToken, String nonce) {
        try {
            SignedJWT jwt = SignedJWT.parse(idToken);
            JWKSet set = JWKSet.load(new URL(prop.getJwksUri()));
            JWK jwk = set.getKeyByKeyId(jwt.getHeader().getKeyID());

            if (jwk instanceof ECKey ec) {
                if (!jwt.verify(new ECDSAVerifier(ec))) {
                    throw new JwtValidationException("bad sig",
                            error(OAuth2ErrorCodes.INVALID_TOKEN, "signature verification failed"));
                }
            } else {
                throw new JwtValidationException("unsupported_key_type",
                        error("unsupported_key_type", "Only EC keys are supported"));
            }

            JWTClaimsSet c = jwt.getJWTClaimsSet();
            if (!prop.getIssuer().equals(c.getIssuer())) {
                throw new JwtValidationException("issuer_mismatch", error("issuer_mismatch", "Invalid issuer"));
            }
            if (!c.getAudience().contains(prop.getClientId())) {
                throw new JwtValidationException("audience_mismatch", error("audience_mismatch", "Invalid audience"));
            }
            if (!nonce.equals(c.getStringClaim("nonce"))) {
                throw new JwtValidationException("nonce_mismatch", error("nonce_mismatch", "Nonce does not match"));
            }
            Date exp = c.getExpirationTime();
            if (exp == null || new Date().after(exp)) {
                throw new JwtValidationException("token_expired",
                        error(OAuth2ErrorCodes.INVALID_TOKEN, "Token is expired or exp claim is missing"));
            }
            return c;
        } catch (ParseException | JOSEException | IOException | JwtValidationException e) {
            log.error("id_token 검증 중 예외 발생", e);
            throw new IllegalArgumentException("id_token 검증 실패");
        }
    }

    private List<OAuth2Error> error(String code, String desc) {
        return List.of(new OAuth2Error(code, desc, null));
    }
}
