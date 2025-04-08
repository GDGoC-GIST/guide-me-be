package guideme.authservice.infrastructure.dto.response.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LoginRedirectionResponse {
    @JsonProperty("redirect_url")
    private final String redirectionUrl;

    public LoginRedirectionResponse(String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }
}
