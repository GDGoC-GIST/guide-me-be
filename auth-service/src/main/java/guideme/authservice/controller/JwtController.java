package guideme.authservice.controller;

import guideme.authservice.domain.token.model.Token;
import guideme.authservice.infrastructure.dto.response.GlobalResponse;
import guideme.authservice.infrastructure.dto.response.login.TokenResponse;
import guideme.authservice.service.token.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
        name = "verify",
        description = "api gateway가 토큰 인증을 위하여 보내는 요청")
public class JwtController {

    private final TokenService tokenService;

    @GetMapping("/verify")
    public GlobalResponse<TokenResponse> verify(
            @RequestHeader("Authorization") String auth
    ) {
        String tokenValue = auth.substring(7);
        try {
            log.info(tokenValue);
            Token token = tokenService.read(tokenValue);
            log.info(token.toString());
            String role = token.getRole();
            String userId = token.getUserId();
            TokenResponse tokenInfo = new TokenResponse(userId, role);
            return GlobalResponse.success(tokenInfo, HttpStatus.OK.value());
        } catch (Exception e) {
            log.info("Invalid access token: {}", e.getMessage());
            return GlobalResponse.fail(null, HttpStatus.UNAUTHORIZED.value());
        }
    }
}
