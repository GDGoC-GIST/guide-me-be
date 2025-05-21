package guideme.authservice.domain.user.event;

import guideme.authservice.domain.user.model.UserDto;
import guideme.authservice.util.clock.ClockHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class UserCreationRollbackEvent extends AuthDomainEvent {
    private static final String EVENT_TYPE = EventType.AUTH_ROLLBACK.getEventName();
    private String userId;
    private String userRole;
    private long eventCreatedAt;

    public static UserCreationRollbackEvent from(UserDto userDto, ClockHolder clockHolder) {
        return new UserCreationRollbackEvent(userDto.getUserId(), userDto.getUserRole(), clockHolder.now());
    }
}
