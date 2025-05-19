package guideme.authservice.service.auth;

import guideme.authservice.infrastructure.dto.response.login.LoginRedirectionResponse;
import guideme.authservice.util.id.IdHolder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import javax.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class OidcAuthService {

    private final IdHolder idHolder;
    private final Cache<String, StatePayload> oauthStateCache;
    private final OidcProperties prop;

    public LoginRedirectionResponse buildRedirect() throws NoSuchAlgorithmException {

        String state = idHolder.generate();
        String nonce = idHolder.generate();
        String codeVerifier = idHolder.generate();
        String codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(
                MessageDigest.getInstance("SHA-256").digest(codeVerifier.getBytes(StandardCharsets.UTF_8)));

        oauthStateCache.put(state, new StatePayload(nonce, codeVerifier, prop.getRedirectUri(), Instant.now()));

        String redirectionUrl = UriComponentsBuilder.fromHttpUrl(prop.getAuthorizeUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", prop.getClientId())
                .queryParam("scope", "openid profile offline_access")
                .queryParam("state", state)
                .queryParam("nonce", nonce)
                .queryParam("redirect_uri", prop.getRedirectUri())
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", "S256")
                .queryParam("prompt", "consent")
                .build()
                .encode()
                .toUriString();

        return new LoginRedirectionResponse(redirectionUrl);

    }
}
