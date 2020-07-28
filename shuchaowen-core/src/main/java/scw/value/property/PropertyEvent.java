package scw.value.property;

import scw.value.event.ValueEvent;

public class PropertyEvent extends ValueEvent {
	private final BasePropertyFactory basePropertyFactory;
	private final String key;

	public PropertyEvent(BasePropertyFactory basePropertyFactory, String key, ValueEvent valueEvent) {
		super(valueEvent);
		this.basePropertyFactory = basePropertyFactory;
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public BasePropertyFactory getBasePropertyFactory() {
		return basePropertyFactory;
	}
}
