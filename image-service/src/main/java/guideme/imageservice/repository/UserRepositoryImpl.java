package guideme.imageservice.repository;

import guideme.imageservice.domain.User;
import guideme.imageservice.repository.entity.UserEntity;
import guideme.imageservice.repository.user.UserJPARepository;
import guideme.imageservice.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJPARepository userJPARepository;

    @Override
    public User findByEmail(String email) {
        return Optional.of(userJPARepository.findByEmail(email)).orElseThrow(() ->
                new EntityNotFoundException("no user")).toModel();
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = UserEntity.fromModel(user);
        UserEntity saved = userJPARepository.save(userEntity);
        return saved.toModel();
    }

    @Override
    public User signup(User user) {
        UserEntity userEntity = userJPARepository.findByUserId(user.getUserId());
        userEntity.setRole(user.getRole());
        userEntity.setSemester(user.getSemester());
        userEntity.setName(user.getName());
        userJPARepository.save(userEntity);
        return user;
    }

    @Override
    public User findByStudentId(String studentId) {
        return Optional.of(userJPARepository.findByStudentId(studentId)).orElseThrow(() ->
                new EntityNotFoundException("no user")).toModel();
    }

    @Override
    public User findById(String userId) {
        return Optional.of(userJPARepository.findByUserId(userId)).orElseThrow(
                () -> new EntityNotFoundException("no user")
        ).toModel();
    }

    @Override
    public boolean checkExistUser(String email) {
        UserEntity userEntity = userJPARepository.findByEmail(email);
        return userEntity != null;
    }
}
