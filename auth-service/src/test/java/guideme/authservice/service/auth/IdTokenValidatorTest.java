package guideme.authservice.service.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * 완전 로컬 환경에서 IdTokenValidator 동작을 검증한다. - 임의의 ES256 EC 키를 만들어 JWKS(JSON) 파일을 temp 디렉터리에 기록 - 동일 키로 id_token(JWT)을 서명 -
 * IdTokenValidator#validate 를 호출해 모든 claim · 서명 검증이 통과하는지 확인
 */
class IdTokenValidatorTest {

    private static IdTokenValidator validator;
    private static String nonce;
    private static String goodToken;

    @BeforeAll
    static void setUp() throws Exception {
        ECKey ecJWK = new ECKeyGenerator(Curve.P_256).keyID("test-kid").generate();

        JWKSet jwkSet = new JWKSet(ecJWK.toPublicJWK());
        File jwksFile = File.createTempFile("jwks", ".json");
        Files.writeString(jwksFile.toPath(), jwkSet.toJSONObject().toString());

        OidcProperties prop = new OidcProperties();
        prop.setIssuer("https://idp.test.local");
        prop.setClientId("guide_me");
        prop.setJwksUri(jwksFile.toURI().toString());

        validator = new IdTokenValidator(prop);

        // 4. id_token 서명
        nonce = "test-nonce-123";
        JWTClaimsSet claims = new JWTClaimsSet.Builder().issuer(prop.getIssuer()).audience(prop.getClientId())
                .subject("user123").claim("nonce", nonce).expirationTime(Date.from(Instant.now().plusSeconds(300)))
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(ecJWK.getKeyID()).type(JOSEObjectType.JWT)
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        jwt.sign(new ECDSASigner(ecJWK));

        goodToken = jwt.serialize();
    }

    @Test
    void validate_withCorrectNonce_returnsClaims() {
        JWTClaimsSet out = validator.validate(goodToken, nonce);
        assertEquals("user123", out.getSubject());
    }

    @Test
    void validate_withWrongNonce_throws() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(goodToken, "wrong-nonce"));
    }
}