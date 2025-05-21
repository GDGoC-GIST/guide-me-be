package guideme.imageservice.domain.user.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserCreationRollbackEvent {
    private String eventType;
    private String userId;
    private String userRole;
    private Long eventCreatedAt;
}
