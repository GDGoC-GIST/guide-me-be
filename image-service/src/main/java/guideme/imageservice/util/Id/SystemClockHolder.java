package guideme.imageservice.util.Id;

public class SystemClockHolder implements ClockHolder {
    @Override
    public long current() {
        return System.currentTimeMillis();
    }
}
