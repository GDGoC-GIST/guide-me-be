package guideme.authservice.service.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import guideme.authservice.infrastructure.dto.response.login.LoginRedirectionResponse;
import guideme.authservice.util.id.IdHolder;
import java.time.Instant;
import javax.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OidcAuthServiceTest {

    private Cache<String, StatePayload> cacheMock;
    private OidcProperties prop;
    private OidcAuthService sut;   // system‑under‑test

    @BeforeEach
    void setUp() {
        IdHolder idHolder = () -> "1234";          // deterministic value
        cacheMock = mock(Cache.class);
        prop = new OidcProperties();
        prop.setAuthorizeUri("https://idp.gistory.me/authorize");
        prop.setClientId("guide_me");
        prop.setRedirectUri("https://api.guide-me.com/api/auth/callback");
        sut = new OidcAuthService(idHolder, cacheMock, prop);
    }

    @Test
    @DisplayName("buildRedirect() → URL에 필수 파라미터 포함 & 캐시에 state 저장")
    void buildRedirect_buildsCorrectUrl_andStoresState() throws Exception {
        LoginRedirectionResponse resp = sut.buildRedirect();
        String url = resp.getRedirectionUrl();

        assertTrue(url.startsWith(prop.getAuthorizeUri()));
        assertTrue(url.contains("response_type=code"));
        assertTrue(url.contains("client_id=" + prop.getClientId()));
        assertTrue(url.contains("state=1234"));
        assertTrue(url.contains("nonce="));
//        assertTrue(url.contains("code_challenge="));
        assertTrue(url.contains("redirect_uri=" + prop.getRedirectUri()));

        verify(cacheMock).put(eq("1234"),
                argThat(sp -> sp != null && sp.nonce() != null && sp.redirectUri().equals(prop.getRedirectUri())
                        && sp.createdAt().isBefore(Instant.now().plusSeconds(1))));
    }
}