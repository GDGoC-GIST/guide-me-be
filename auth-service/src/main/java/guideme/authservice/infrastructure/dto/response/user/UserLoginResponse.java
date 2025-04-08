package guideme.authservice.infrastructure.dto.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import guideme.authservice.domain.user.model.User;
import guideme.authservice.infrastructure.dto.TokenPairResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserLoginResponse {

    private static final String SIGN_IN = "signIn";
    private static final String SIGN_UP = "signUp";

    private final String returnType;
    @JsonProperty("userInfo")
    private final User user;
    @JsonProperty("token")
    private final TokenPairResponse tokenPairResponse;

    @Builder(access = AccessLevel.PRIVATE)
    private UserLoginResponse(String returnType, TokenPairResponse tokenPairResponse, User user) {
        this.returnType = returnType;
        this.tokenPairResponse = tokenPairResponse;
        this.user = user;
    }

    public static UserLoginResponse of(User user, TokenPairResponse tokenPairResponse, boolean isSignUp) {
        if (isSignUp) {
            return UserLoginResponse.builder()
                    .returnType(SIGN_UP)
                    .tokenPairResponse(tokenPairResponse)
                    .user(user)
                    .build();
        }
        return UserLoginResponse.builder()
                .returnType(SIGN_IN)
                .tokenPairResponse(tokenPairResponse)
                .user(user)
                .build();
    }
}
