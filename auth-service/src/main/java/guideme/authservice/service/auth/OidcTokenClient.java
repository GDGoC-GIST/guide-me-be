package guideme.authservice.service.auth;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OidcTokenClient {

    private final RestTemplate rest;
    private final OidcProperties prop;

    public OidcTokenClient(OidcProperties prop, RestTemplateBuilder restTemplateBuilder) {
        this.prop = prop;
        this.rest = restTemplateBuilder.connectTimeout(Duration.ofSeconds(2)).readTimeout(Duration.ofSeconds(5))
                .build();
    }

    public ResponseEntity<TokenResponse> exchangeCode(String code, StatePayload s) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", s.redirectUri());
        body.add("code_verifier", s.codeVerifier());

        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        h.setBasicAuth(prop.getClientId(), prop.getClientSecret());
        return rest.postForEntity(prop.getTokenUri(), new HttpEntity<>(body, h), TokenResponse.class);
    }
}
