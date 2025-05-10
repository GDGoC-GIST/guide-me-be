package guideme.imageservice.domain;

import guideme.imageservice.dto.UserValidCheckRequest;
import guideme.imageservice.util.clock.ClockHolder;
import guideme.imageservice.util.Id.IdHolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class User {
    private final String userId;
    private final String email;
    private final String name;
    private final String studentId;
    private final Integer semester;
    private final UserRole role;
    private final Long createdAt;
    private final Long deletedAt;

    public static User create(IdHolder idHolder, ClockHolder clockHolder, UserValidCheckRequest userValidCheckRequest) {
        // name, semester -> null
        return User.builder()
                .userId(idHolder.generate())
                .email(userValidCheckRequest.getEmail())
                .studentId(userValidCheckRequest.getStudentId())
                .role(UserRole.PENDING)
                .createdAt(clockHolder.current())
                .build();
    }

    public User signUp(String name, Integer semester) {
        return toBuilder().name(name).semester(semester).role(UserRole.ABLE).build();
    }
}
