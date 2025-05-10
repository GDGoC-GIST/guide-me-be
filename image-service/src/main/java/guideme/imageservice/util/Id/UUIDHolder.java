package guideme.imageservice.util.Id;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UUIDHolder implements IdHolder {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
