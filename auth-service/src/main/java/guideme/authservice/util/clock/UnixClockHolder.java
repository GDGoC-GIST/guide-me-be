package guideme.authservice.util.clock;

import org.springframework.stereotype.Component;

@Component
public class UnixClockHolder implements ClockHolder {
    @Override
    public long now() {
        return System.currentTimeMillis();
    }
}
