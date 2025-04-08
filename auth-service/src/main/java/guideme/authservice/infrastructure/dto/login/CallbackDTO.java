package guideme.authservice.infrastructure.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CallbackDTO {
    @JsonProperty("is_pending")
    private boolean isPending;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("user_info")
    private UserInfo userInfo;
}

@Getter
@Setter
@NoArgsConstructor
class UserInfo {
    @JsonProperty("user_id")
    private String userId;
    private String nickname;
    private String email;
    private Integer semester;

}