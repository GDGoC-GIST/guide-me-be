package guideme.imageservice.controller;

import guideme.imageservice.dto.UserResponse;
import guideme.imageservice.dto.UserValidCheckRequest;
import guideme.imageservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api/user")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public UserResponse userLogin(
            @RequestParam String email,
            @RequestParam(name = "student_id") String studentId
    ) {
            return userService.userCreate(new UserValidCheckRequest(studentId, email));
    }
}
