package guideme.imageservice.service;

import guideme.imageservice.domain.User;
import guideme.imageservice.dto.UserSignUpRequest;
import guideme.imageservice.dto.UserValidCheckRequest;
import guideme.imageservice.dto.UserResponse;
import guideme.imageservice.repository.user.UserRepository;
import guideme.imageservice.util.clock.ClockHolder;
import guideme.imageservice.util.Id.IdHolder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final IdHolder idHolder;
    private final ClockHolder clockHolder;



    // user make -> 가회원가입
    public UserResponse userCreate(
            UserValidCheckRequest userValidCheckRequest
    ) {
        try {
            User user = userRepository.findByEmail(userValidCheckRequest.getEmail());
            return UserResponse.create(user);
        } catch (EntityNotFoundException e) {
            User user = User.create(idHolder, clockHolder, userValidCheckRequest);
            user = userRepository.save(user);
            return UserResponse.create(user);
        }
    }

    // user info update -> 회원가입
    public UserResponse userSignUp(
            String userId, UserSignUpRequest userSignUpRequest
    ) {
        User user = userRepository.findById(userId);
        user = user.signUp(userSignUpRequest.getName(), userSignUpRequest.getSemester());
        user = userRepository.signup(user);
        return UserResponse.create(user);
    }

    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId);
        return UserResponse.create(user);
    }
}
