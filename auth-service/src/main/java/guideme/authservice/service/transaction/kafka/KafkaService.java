package guideme.authservice.service.transaction.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guideme.authservice.domain.user.event.AuthDomainEvent;
import guideme.authservice.domain.user.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendEvent(EventType eventType, AuthDomainEvent event) {
        try {
            String stringEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(eventType.getEventTopic(), stringEvent);
        } catch (JsonProcessingException | RuntimeException e) {
            log.error("ROLLBACK FAILED : {}", event.toString());
            throw new RuntimeException(e);
        }
    }
}
