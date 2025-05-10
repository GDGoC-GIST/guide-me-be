package guideme.imageservice.controller;

import guideme.imageservice.dto.UserResponse;
import guideme.imageservice.dto.UserSignUpRequest;
import guideme.imageservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class SignUpController {

    private final UserService userService;

    @PostMapping()
    public UserResponse signUp(
            @RequestHeader(name = "X-Client-Id") String userId,
            @RequestBody UserSignUpRequest userSignUpRequest
    ) {
        return userService.userSignUp(userId, userSignUpRequest);
    }

    @GetMapping
    public UserResponse getUser(
            @RequestHeader(name = "X-Client-Id") String userId
    ) {
        return userService.getUserById(userId);
    }

}
