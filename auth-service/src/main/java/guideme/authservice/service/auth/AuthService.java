package guideme.authservice.service.auth;

import com.nimbusds.jwt.JWTClaimsSet;
import guideme.authservice.domain.user.event.EventType;
import guideme.authservice.domain.user.event.UserCreationRollbackEvent;
import guideme.authservice.domain.user.model.UserDto;
import guideme.authservice.infrastructure.dto.TokenPairResponse;
import guideme.authservice.infrastructure.dto.response.login.LoginRedirectionResponse;
import guideme.authservice.infrastructure.dto.response.user.UserLoginResponse;
import guideme.authservice.service.auth.user.UserClient;
import guideme.authservice.service.token.TokenService;
import guideme.authservice.service.transaction.kafka.KafkaService;
import guideme.authservice.util.clock.ClockHolder;
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

    private static final String EMAIL_CLAIM = "email";
    private static final String STUDENT_ID_CLAIM = "student_id";
    private static final String ROLE_PENDING = "PENDING";

    private final TokenService tokenService;
    private final Cache<String, StatePayload> oauthStateCache;
    private final KafkaService kafkaService;
    private final ClockHolder clockHolder;
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
        StatePayload payload = getCachedPayload(state);
        try {
            ResponseEntity<TokenResponse> response = oidcTokenClient.exchangeCode(code, payload);
            return extractTokenAndLoginResponse(response, payload);
        } finally {
            oauthStateCache.remove(state);
        }
    }

    private UserLoginResponse buildLoginResponseFromClaims(JWTClaimsSet claims) {
        try {
            String email = claims.getStringClaim(EMAIL_CLAIM);
            String studentId = claims.getStringClaim(STUDENT_ID_CLAIM);
            return getUserLoginResponse(email, studentId);
        } catch (ParseException e) {
            throw new IllegalStateException("parseError", e);
        } catch (Exception e) {
            throw new IllegalStateException("tokenCreationError", e);
        }
    }

    private UserLoginResponse getUserLoginResponse(String email, String studentId) {
        UserDto userDto = userClient.findOrSignUp(email, studentId);
        try {
            TokenPairResponse tokenPair = tokenService.generateTokenPair(userDto);
            return UserLoginResponse.of(userDto, tokenPair, userDto.getUserRole().equals(ROLE_PENDING));
        } catch (Exception e) {
            if (userDto.getUserRole().equals(ROLE_PENDING)) {
                kafkaService.sendEvent(EventType.AUTH_ROLLBACK, UserCreationRollbackEvent.from(userDto, clockHolder));
            }
            throw e;
        }
    }

    private StatePayload getCachedPayload(String state) {
        StatePayload payload = oauthStateCache.get(state);
        if (payload == null) {
            throw new IllegalArgumentException("state가 잘못되었습니다.");
        }
        return payload;
    }

    private UserLoginResponse extractTokenAndLoginResponse(ResponseEntity<TokenResponse> response,
                                                           StatePayload payload) {
        String idToken = validateResponseAndExtractIdToken(response);
        JWTClaimsSet claims = idTokenValidator.validate(idToken, payload.nonce());
        return buildLoginResponseFromClaims(claims);
    }

    private String validateResponseAndExtractIdToken(ResponseEntity<TokenResponse> response) {
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalArgumentException("Failed to retrieve token.");
        }
        String idToken = response.getBody().id_token();
        if (idToken == null) {
            throw new IllegalArgumentException("토큰 응답에 access_token 또는 id_token이 없습니다.");
        }
        return idToken;
    }

}
