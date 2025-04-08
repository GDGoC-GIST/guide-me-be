package guideme.authservice.service.auth;

import guideme.authservice.domain.user.model.User;
import guideme.authservice.infrastructure.dto.TokenPairResponse;
import guideme.authservice.infrastructure.dto.response.login.LoginRedirectionResponse;
import guideme.authservice.infrastructure.dto.response.user.UserLoginResponse;
import guideme.authservice.infrastructure.dto.user.LoginAccessUser;
import guideme.authservice.infrastructure.dto.user.UserInfoChecker;
import guideme.authservice.service.token.TokenService;
import guideme.authservice.util.id.IdHolder;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AuthService {

    private static final String OAUTH_SECRET = "secret";
    private static final String OAUTH_CLIENT_NAME = "guide_me";
    private static final String REDIRECT_URI = "https://api.guide-me.com/api/auth/callback";
    private static final String TOKEN_URL = "https://api.idp.gistory.me/oauth2/token";
    private static final String AUTHORIZE_URL = "https://idp.gistory.me/authorize";
    private static final String GRENT_TYPE = "authorization_code";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private final IdHolder idHolder;
    private final UserChecker userChecker;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    public AuthService(IdHolder idHolder, UserChecker userChecker, TokenService tokenService) {
        this.idHolder = idHolder;
        this.userChecker = userChecker;
        this.tokenService = tokenService;
        this.restTemplate = new RestTemplate();
    }

    public LoginRedirectionResponse getLoginUrl() {
        String state = idHolder.generate();
        String nonce = idHolder.generate();
        String redirectionUrl = String.format(
                "%s?response_type=code&client_id=%s&scope=openid%20profile&state=%s&redirect_uri=%s&prompt=none",
                AUTHORIZE_URL, OAUTH_CLIENT_NAME, state, REDIRECT_URI);
        return new LoginRedirectionResponse(redirectionUrl);
    }

    public UserLoginResponse getAccessToken(String code, String state) {
        // TODO: 저장된 state와 전달받은 state값을 비교 및 검증
        HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = buildTokenRequestEntity(code);
        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, requestEntity, Map.class);
        return extractAccessTokenAndLoginResponse(response);
    }

    private UserLoginResponse extractAccessTokenAndLoginResponse(ResponseEntity<Map> response) {
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Failed to retrieve access token. Response: {}", response);
            throw new IllegalArgumentException("Failed to retrieve access token.");
        }
        Map<?, ?> body = response.getBody();
        Object tokenObj = body.get(ACCESS_TOKEN_KEY);
        if (tokenObj instanceof String) {
            return getUserLoginResponse((String) tokenObj);
        }
        log.error("Access token missing in the response body: {}", body);
        throw new IllegalArgumentException("Access token retrieval error.");
    }

    private HttpEntity<LinkedMultiValueMap<String, String>> buildTokenRequestEntity(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(OAUTH_CLIENT_NAME, OAUTH_SECRET);

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", GRENT_TYPE);
        params.add("code", code);
        params.add("redirect_url", REDIRECT_URI);

        return new HttpEntity<>(params, headers);
    }

    public UserLoginResponse getUserLoginResponse(String accessToken) {
        LoginAccessUser accessUser = userChecker.getUserInfo(accessToken);
        UserInfoChecker userInfoCheck = userChecker.getUserInfoCheck(accessUser);
        User user = User.fromChecker(userInfoCheck);
        TokenPairResponse tokenPairResponse = tokenService.generateTokenPair(user);
        return createUserLoginResponse(user, tokenPairResponse);
    }

    private UserLoginResponse createUserLoginResponse(User user, TokenPairResponse tokenPairResponse) {
        boolean isPending = user.getUserRole().equals("pending");
        return UserLoginResponse.of(user, tokenPairResponse, isPending);
    }
}
