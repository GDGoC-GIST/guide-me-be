package guideme.imageservice.repository.entity;

import guideme.imageservice.domain.User;
import guideme.imageservice.domain.UserRole;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(nullable = false)
    private String email;

    private String name;

    @Column(nullable = false)
    private String studentId;

    private Integer semester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "deleted_at")
    private Long deletedAt;

    public User toModel() {
        return User.builder()
                .userId(userId)
                .email(email)
                .name(name)
                .studentId(studentId)
                .semester(semester)
                .role(role)
                .createdAt(createdAt)
                .deletedAt(deletedAt)
                .build();
    }

    public static UserEntity fromModel(User user) {
        return UserEntity.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .studentId(user.getStudentId())
                .semester(user.getSemester())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }
}
