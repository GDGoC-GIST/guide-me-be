package guideme.authservice.infrastructure.dto.user;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class UserInfoChecker {
    private String userId;
    private String email;
    private String userRole;
    private String studentId;

    @Nullable
    private String nickname;

    @Nullable
    private Integer semester;


}
