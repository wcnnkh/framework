package scw.value.event;

import scw.event.support.BasicEvent;
import scw.event.support.EventType;
import scw.value.Value;

public class ValueEvent extends BasicEvent {
	private final Value value;

	public ValueEvent(ValueEvent valueEvent) {
		super(valueEvent);
		this.value = valueEvent.getValue();
	}

	public ValueEvent(EventType eventType, Value value) {
		super(eventType);
		this.value = value;
	}

	public Value getValue() {
		return value;
	}

	@Override
	public String toString() {
		return super.toString() + ", value=" + value;
	}
}
