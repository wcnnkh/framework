package scw.event.support;

public class ValueEvent<V> extends BasicTypeEvent {
	private final V value;

	public ValueEvent(ValueEvent<V> valueEvent) {
		super(valueEvent);
		this.value = valueEvent.value;
	}

	public ValueEvent(EventType eventType, V value) {
		super(eventType);
		this.value = value;
	}

	public V getValue() {
		return value;
	}

	@Override
	public String toString() {
		return super.toString() + ", value=" + value;
	}
}
