package guideme.imageservice.util.clock;

import org.springframework.stereotype.Component;

@Component
public class SystemClockHolder implements ClockHolder {
    @Override
    public long current() {
        return System.currentTimeMillis();
    }
}
