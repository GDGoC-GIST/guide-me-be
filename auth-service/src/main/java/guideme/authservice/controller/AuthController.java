package guideme.authservice.controller;

import guideme.authservice.infrastructure.dto.response.GlobalResponse;
import guideme.authservice.infrastructure.dto.response.login.LoginRedirectionResponse;
import guideme.authservice.infrastructure.dto.response.user.UserLoginResponse;
import guideme.authservice.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public GlobalResponse<LoginRedirectionResponse> getLoginRequest() {
        LoginRedirectionResponse loginUrl = authService.getLoginUrl();
        return GlobalResponse.success(loginUrl, 200);
    }


    @GetMapping("/callback")
    public GlobalResponse<UserLoginResponse> gatAccessToken(
            @RequestParam("code") String code,
            @RequestParam("state") String state
    ) {
        UserLoginResponse accessToken = authService.getAccessToken(code, state);
        return GlobalResponse.success(accessToken, 200);
    }

//    @PostMapping("/refresh")
//    public void createNewAccessToken() {
//
//    }
}
