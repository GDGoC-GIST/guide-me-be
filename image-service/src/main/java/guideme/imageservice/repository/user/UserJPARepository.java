package guideme.imageservice.repository.user;

import guideme.imageservice.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJPARepository extends JpaRepository<UserEntity, String> {
    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);

    UserEntity findByStudentId(String studentId);
}
