package guideme.authservice.infrastructure.dto.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import guideme.authservice.domain.user.model.UserDto;
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
    private final UserDto userDto;
    @JsonProperty("token")
    private final TokenPairResponse tokenPairResponse;

    @Builder(access = AccessLevel.PRIVATE)
    private UserLoginResponse(String returnType, TokenPairResponse tokenPairResponse, UserDto userDto) {
        this.returnType = returnType;
        this.tokenPairResponse = tokenPairResponse;
        this.userDto = userDto;
    }

    public static UserLoginResponse of(UserDto userDto, TokenPairResponse tokenPairResponse, boolean isSignUp) {
        if (isSignUp) {
            return UserLoginResponse.builder()
                    .returnType(SIGN_UP)
                    .tokenPairResponse(tokenPairResponse)
                    .userDto(userDto)
                    .build();
        }
        return UserLoginResponse.builder()
                .returnType(SIGN_IN)
                .tokenPairResponse(tokenPairResponse)
                .userDto(userDto)
                .build();
    }
}
