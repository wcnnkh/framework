package scw.value.property;

import scw.event.support.ValueEvent;
import scw.value.Value;

public class PropertyEvent extends ValueEvent<Value> {
	private final BasePropertyFactory basePropertyFactory;
	private final String key;

	public PropertyEvent(BasePropertyFactory basePropertyFactory, String key, ValueEvent<Value> valueEvent) {
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

	@Override
	public String toString() {
		return "propertyFactory=" + basePropertyFactory + ", key=" + key + ", " + super.toString();
	}
}
