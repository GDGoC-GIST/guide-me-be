package guideme.authservice.controller;

import guideme.authservice.infrastructure.dto.response.GlobalResponse;
import guideme.authservice.infrastructure.dto.response.login.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/internal/api/auth")
@RequiredArgsConstructor
public class JwtController {

    @GetMapping("/verify")
    public GlobalResponse<TokenResponse> verify() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return GlobalResponse.fail(null, HttpStatus.UNAUTHORIZED.value());
        }

        String userId = authentication.getName();
        String role = authentication.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority)
                .orElse(null);

        TokenResponse tokenInfo = new TokenResponse(userId, role);
        return GlobalResponse.success(tokenInfo, HttpStatus.OK.value());
    }
}
