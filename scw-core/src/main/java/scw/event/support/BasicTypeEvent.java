package scw.event.support;

public class BasicTypeEvent extends BasicEvent {
	private final EventType eventType;

	public BasicTypeEvent(EventType eventType) {
		this(eventType, System.currentTimeMillis());
	}

	public BasicTypeEvent(EventType eventType, long createTime) {
		super(createTime);
		this.eventType = eventType;
	}

	public BasicTypeEvent(BasicTypeEvent event) {
		super(event);
		this.eventType = event.eventType;
	}

	public EventType getEventType() {
		return eventType;
	}

	@Override
	public String toString() {
		return "eventType=" + eventType + ", " + super.toString();
	}
}
