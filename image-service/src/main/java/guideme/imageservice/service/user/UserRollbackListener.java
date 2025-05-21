package guideme.imageservice.service.user;

import guideme.imageservice.domain.user.event.UserCreationRollbackEvent;
import guideme.imageservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRollbackListener {

    private final UserService userService;

    @KafkaListener(topics = "auth.user.pending-rollback", containerFactory = "kafkaListenerContainerFactory")
    public void handleRollbackEvent(UserCreationRollbackEvent event) {
        log.info("Rollback event 수신 : {}", event);
        userService.rollbackUserCreate(event);
    }
}
