package io.basc.framework.event;

public class ChangeEvent<T> extends ObjectEvent<T> {
	private static final long serialVersionUID = 1L;
	private final EventType eventType;

	public ChangeEvent(EventType eventType, ObjectEvent<T> event) {
		super(event);
		this.eventType = eventType;
	}

	public ChangeEvent(EventType eventType, T source) {
		super(source);
		this.eventType = eventType;
	}

	public ChangeEvent(ChangeEvent<T> event) {
		super(event);
		this.eventType = event.eventType;
	}

	public ChangeEvent(ChangeEvent<?> event, T source) {
		super(source, event.getCreateTime());
		this.eventType = event.eventType;
	}

	public EventType getEventType() {
		return eventType;
	}
}
