package guideme.imageservice.util.Id;

import java.util.UUID;

public class UUIDHolder implements IdHolder {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
