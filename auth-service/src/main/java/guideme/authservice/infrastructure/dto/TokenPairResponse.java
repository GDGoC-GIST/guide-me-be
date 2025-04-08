package guideme.authservice.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenPairResponse {
    @JsonProperty("token_type")
    private static final String TOKEN_TYPE = "Bearer";
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("access_token")
    private String refreshToken;
}
