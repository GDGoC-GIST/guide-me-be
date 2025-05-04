package guideme.authservice.service.auth;

import com.nimbusds.jwt.JWTClaimsSet;
import guideme.authservice.domain.user.model.UserDto;
import guideme.authservice.infrastructure.dto.TokenPairResponse;
import guideme.authservice.infrastructure.dto.response.login.LoginRedirectionResponse;
import guideme.authservice.infrastructure.dto.response.user.UserLoginResponse;
import guideme.authservice.service.auth.user.UserClient;
import guideme.authservice.service.token.TokenService;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import javax.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;

    private final Cache<String, StatePayload> oauthStateCache;


    private final IdTokenValidator idTokenValidator;
    private final OidcTokenClient oidcTokenClient;
    private final OidcAuthService oidcAuthService;
    private final UserClient userClient;


    public LoginRedirectionResponse getLoginUrl() {
        try {
            return oidcAuthService.buildRedirect();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("no supported algorithm", e);
        }
    }

    public UserLoginResponse getAccessToken(String code, String state) {
        StatePayload payload = oauthStateCache.get(state);
        if (payload == null) {
            throw new IllegalArgumentException("state가 잘못되었습니다.");
        }
        try {
            ResponseEntity<TokenResponse> response = oidcTokenClient.exchangeCode(code, payload);
            return extractAccessTokenAndLoginResponse(response, payload);
        } finally {
            oauthStateCache.remove(state);
        }
    }

    private UserLoginResponse extractAccessTokenAndLoginResponse(ResponseEntity<TokenResponse> response,
                                                                 StatePayload payload) {
        String idToken = validateResponseAndExtractIdToken(response);
        JWTClaimsSet claims = idTokenValidator.validate(idToken, payload.nonce());
        return buildLoginResponseFromClaims(claims);
    }

    private String validateResponseAndExtractIdToken(ResponseEntity<TokenResponse> response) {
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalArgumentException("Failed to retrieve token.");
        }
        TokenResponse body = response.getBody();
        if (body == null) {
            throw new IllegalArgumentException("response body is null");
        }
        String idToken = body.id_token();
        if (idToken == null) {
            throw new IllegalArgumentException("토큰 응답에 access_token 또는 id_token이 없습니다.");
        }
        return idToken;
    }

    private UserLoginResponse buildLoginResponseFromClaims(JWTClaimsSet claims) {
        try {
            String email = claims.getStringClaim("email");
            String studentId = claims.getStringClaim("studentid");
            UserDto userDto = userClient.findOrSignUp(email, studentId);
            TokenPairResponse tokenPair = tokenService.generateTokenPair(userDto);
            return UserLoginResponse.of(userDto, tokenPair, userDto.getUserRole().equals("pending"));
        } catch (ParseException e) {
            throw new IllegalStateException("parseError");
        }
    }

}
