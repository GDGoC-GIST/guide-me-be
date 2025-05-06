package guideme.authservice.service.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth.oidc")
@Data
public class OidcProperties {
    private String issuer;
    private String clientId;
    private String clientSecret;
    private String authorizeUri;
    private String tokenUri;
    private String jwksUri;
    private String redirectUri;
}
