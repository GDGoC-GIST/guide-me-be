package guideme.apigateway.client;

import guideme.apigateway.dto.AuthVerificationResponse;
import guideme.apigateway.dto.GlobalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @PostMapping("/internal/api/auth/verify")
    GlobalResponse<AuthVerificationResponse> verify(@RequestHeader("Authorization")String token);
}
