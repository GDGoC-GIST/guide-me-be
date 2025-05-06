package guideme.authservice.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import guideme.authservice.domain.token.model.Token;
import java.net.URI;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;
import javax.cache.Cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class OidcTokenClientTest {

    @Mock
    RestTemplateBuilder builder;

    @Mock
    RestTemplate rest;

    OidcTokenClient client;   // will be reassigned in setUp

    private OidcProperties prop;

    @BeforeEach
    void setUp() {
        when(builder.connectTimeout(any(Duration.class))).thenReturn(builder);
        when(builder.readTimeout(any(Duration.class))).thenReturn(builder);
        when(builder.build()).thenReturn(rest);


        prop = new OidcProperties();
        prop.setTokenUri("https://api.idp.gistory.me/oauth/token");
        prop.setClientId("guide_me");
        prop.setClientSecret("topsecret");

        client = new OidcTokenClient(prop, builder);
    }

    @Test
    @DisplayName("exchangeCode() → POST /oauth/token 호출 & Response 반환")
    void exchangeCode_callsTokenEndpoint() {
        // given
        StatePayload p = new StatePayload("nonce","ver","https://cb", java.time.Instant.now());
        TokenResponse mockResp = new TokenResponse("atk",3600,null);
        ResponseEntity<TokenResponse> restResp = ResponseEntity.ok(mockResp);
        when(rest.postForEntity(anyString(), any(), eq(TokenResponse.class))).thenReturn(restResp);

        // when
        ResponseEntity<TokenResponse> out = client.exchangeCode("abc123", p);

        // then response 그대로 반환
        assertSame(restResp, out);
        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(rest).postForEntity(eq(prop.getTokenUri()), captor.capture(), eq(TokenResponse.class));

        HttpEntity sent = captor.getValue();
        assertTrue(Objects.requireNonNull(sent.getHeaders().getContentType()).includes(MediaType.APPLICATION_FORM_URLENCODED));
        assertEquals("Basic " + Base64.getEncoder()
                .encodeToString((prop.getClientId()+":"+prop.getClientSecret()).getBytes()),
                sent.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));

        // form params
        assertInstanceOf(MultiValueMap.class, sent.getBody());
        MultiValueMap<String, String> body = (MultiValueMap<String, String>) sent.getBody();
        assertEquals("authorization_code", body.getFirst("grant_type"));
        assertEquals("abc123", body.getFirst("code"));
        assertEquals(p.redirectUri(), body.getFirst("redirect_uri"));
//        assertEquals(p.codeVerifier(), body.getFirst("code_verifier"));
    }
}