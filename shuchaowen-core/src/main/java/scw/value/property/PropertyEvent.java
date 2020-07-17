package scw.value.property;

import scw.event.support.BasicEvent;
import scw.event.support.EventType;
import scw.value.Value;

public class PropertyEvent extends BasicEvent {
	private final String key;
	private final Value value;

	public PropertyEvent(EventType eventType, String key, Value value) {
		super(eventType);
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public Value getValue() {
		return value;
	}
}
