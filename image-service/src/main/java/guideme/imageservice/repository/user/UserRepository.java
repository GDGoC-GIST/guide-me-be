package guideme.imageservice.repository.user;

import guideme.imageservice.domain.User;

public interface UserRepository {

    User save(User user);

    User signup(User user);

    User findByEmail(String email);

    User findByStudentId(String studentId);

    boolean checkExistUser(String userId);

    User findById(String userId);
}

