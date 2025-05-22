package guideme.authservice.domain.user.event;

public enum EventType {

    AUTH_ROLLBACK("AUTH_ROLLBACK", "e");

    private final String eventName;
    private final String eventTopic;
    EventType(final String eventName, final String eventType) {
        this.eventName = eventName;
        this.eventTopic = eventType;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventTopic() {
        return eventTopic;
    }
}
