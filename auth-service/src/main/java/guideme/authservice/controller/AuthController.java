package guideme.authservice.controller;

import guideme.authservice.infrastructure.dto.response.GlobalResponse;
import guideme.authservice.infrastructure.dto.response.login.LoginRedirectionResponse;
import guideme.authservice.infrastructure.dto.response.user.UserLoginResponse;
import guideme.authservice.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "auth", description = "로그인 인증 관련 api")
public class AuthController {

    private final AuthService authService;

    @GetMapping()
    @Operation(
            summary = "로그인 요청",
            description = "클라이언트가 인증을 시작할 수 있도록 로그인 URL을 제공합니다."
    )
    public GlobalResponse<LoginRedirectionResponse> getLoginRequest() {
        LoginRedirectionResponse loginUrl = authService.getLoginUrl();
        return GlobalResponse.success(loginUrl, HttpStatus.OK.value());
    }


    @GetMapping("/callback")
    @Operation(
            summary = "로그인 콜백",
            description = "OIDC 서비스 제공자가 호출하여 클라이언트 인증을 완료합니다."
    )
    public GlobalResponse<UserLoginResponse> gatAccessToken(@RequestParam("code") String code,
                                                            @RequestParam("state") String state) {
        UserLoginResponse accessToken = authService.getAccessToken(code, state);
        return GlobalResponse.success(accessToken, 200);
    }
}
