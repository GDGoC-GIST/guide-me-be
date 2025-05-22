package guideme.imageservice.repository.user;

import guideme.imageservice.domain.user.User;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    User signup(User user);

    Optional<User> findByEmail(String email);

    User findByStudentId(String studentId);

    boolean checkExistUser(String userId);

    User findById(String userId);

    void delete(String userId);
}

