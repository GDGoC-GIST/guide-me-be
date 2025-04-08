package guideme.authservice.infrastructure.dto.user;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserInfoChecker {
    private String userId;
    private String email;
    private String userRole;
    private String studentId;

    @Nullable
    private String nickname;
    private Integer semester;


}
