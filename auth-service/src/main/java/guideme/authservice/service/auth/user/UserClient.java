package guideme.authservice.service.auth.user;

import guideme.authservice.domain.user.model.UserDto;
import guideme.authservice.infrastructure.dto.user.LoginAccessUser;
import guideme.authservice.infrastructure.dto.user.UserInfoChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserClient {

    private final UserChecker userChecker;

    public UserDto findOrSignUp(String email, String studentId) {
        LoginAccessUser loginAccessUser = new LoginAccessUser(email, studentId);
        UserInfoChecker userInfoCheck = userChecker.getUserInfoCheck(loginAccessUser);
        return UserDto.fromChecker(userInfoCheck);
    }
}
