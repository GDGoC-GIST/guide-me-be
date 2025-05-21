package guideme.imageservice.service;

import guideme.imageservice.domain.user.User;
import guideme.imageservice.domain.user.event.UserCreationRollbackEvent;
import guideme.imageservice.dto.UserResponse;
import guideme.imageservice.dto.UserSignUpRequest;
import guideme.imageservice.dto.UserValidCheckRequest;
import guideme.imageservice.repository.user.UserRepository;
import guideme.imageservice.util.Id.IdHolder;
import guideme.imageservice.util.clock.ClockHolder;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final IdHolder idHolder;
    private final ClockHolder clockHolder;

    public UserResponse userCreate(UserValidCheckRequest userValidCheckRequest) {
        Optional<User> user = userRepository.findByEmail(userValidCheckRequest.getEmail());
        if (user.isPresent()) {
            return UserResponse.create(user.get());
        }
        User createdUser = User.create(idHolder, clockHolder, userValidCheckRequest);
        createdUser = userRepository.save(createdUser);
        return UserResponse.create(createdUser);
    }

    public UserResponse userSignUp(String userId, UserSignUpRequest userSignUpRequest) {
        User user = userRepository.findById(userId);
        user = user.signUp(userSignUpRequest.getName(), userSignUpRequest.getSemester());
        user = userRepository.signup(user);
        return UserResponse.create(user);
    }

    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId);
        return UserResponse.create(user);
    }

    @Transactional
    public void rollbackUserCreate(UserCreationRollbackEvent event) {
        try {
            User user = userRepository.findById(event.getUserId());
            if (!user.getRole().name().equals(event.getUserRole())) {
                throw new IllegalStateException(
                        "User with id " + user.getUserId() + " does not belong to user role " + event.getEventType());
            }
            userRepository.delete(event.getUserId());
            log.info("USER ROLLBACK TRANSACTION COMPLETED : {}", event);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            log.error("USER ROLLBACK TRANSACTION FAILED : {}", e.getMessage());
        }
    }
}
