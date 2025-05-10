package guideme.imageservice.dto;

import guideme.imageservice.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserResponse {
    private String userId;
    private String email;
    private String userRole;
    private String studentId;

    private String nickname;
    private Integer semester;

    public static UserResponse create(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userRole(user.getRole().toString())
                .studentId(user.getStudentId())
                .nickname(user.getName())
                .semester(user.getSemester())
                .build();
    }
}
