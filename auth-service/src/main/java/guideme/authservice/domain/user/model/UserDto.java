package guideme.authservice.domain.user.model;

import guideme.authservice.infrastructure.dto.user.UserInfoChecker;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDto {
    private final String userId;
    private final String userRole;
    private final String studentId;
    private final String email;

    @Nullable
    private final int semester;
    private final String nickname;

    @Builder(access = AccessLevel.PACKAGE)
    private UserDto(String userId, String userRole, String studentId, String email, String nickname, int semester) {
        this.userId = userId;
        this.userRole = userRole;
        this.studentId = studentId;
        this.email = email;
        this.nickname = nickname;
        this.semester = semester;
    }

    public static UserDto fromChecker(UserInfoChecker checker) {
        UserDto.UserDtoBuilder builder = UserDto.builder()
                .userId(checker.getUserId())
                .userRole(checker.getUserRole())
                .studentId(checker.getStudentId())
                .email(checker.getEmail());

        if (!"PENDING".equals(checker.getUserRole())) {
            builder.nickname(checker.getNickname())
                    .semester(checker.getSemester());
        }
        return builder.build();
    }

}
