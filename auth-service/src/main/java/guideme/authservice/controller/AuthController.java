package guideme.authservice.controller;

import guideme.authservice.infrastructure.dto.response.GlobalResponse;
import guideme.authservice.infrastructure.dto.response.login.LoginRedirectionResponse;
import guideme.authservice.infrastructure.dto.response.user.UserLoginResponse;
import guideme.authservice.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth/login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping()
    public GlobalResponse<LoginRedirectionResponse> getLoginRequest() {
        LoginRedirectionResponse loginUrl = authService.getLoginUrl();
        return GlobalResponse.success(loginUrl, HttpStatus.OK.value());
    }


    @GetMapping("/callback")
    public GlobalResponse<UserLoginResponse> gatAccessToken(@RequestParam("code") String code,
                                                            @RequestParam("state") String state) {
        UserLoginResponse accessToken = authService.getAccessToken(code, state);
        return GlobalResponse.success(accessToken, 200);
    }
}
