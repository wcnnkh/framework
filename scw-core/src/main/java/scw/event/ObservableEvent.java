package scw.event;


public class ObservableEvent<T> extends ObjectEvent<T>{
	private static final long serialVersionUID = 1L;
	private final EventType eventType;
	
	public ObservableEvent(EventType eventType, T source) {
		super(source);
		this.eventType = eventType;
	}

	public EventType getEventType() {
		return eventType;
	}
}
