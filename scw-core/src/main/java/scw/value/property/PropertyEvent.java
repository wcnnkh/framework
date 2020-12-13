package scw.value.property;

import scw.event.EventType;
import scw.event.KeyValuePairEvent;
import scw.value.Value;

@SuppressWarnings("serial")
public class PropertyEvent extends KeyValuePairEvent<String, Value> {
	private final BasePropertyFactory basePropertyFactory;

	public PropertyEvent(BasePropertyFactory basePropertyFactory, KeyValuePairEvent<String, Value> event) {
		super(event);
		this.basePropertyFactory = basePropertyFactory;
	}
	
	public PropertyEvent(BasePropertyFactory basePropertyFactory, EventType eventType, String key, Value value) {
		super(eventType, key, value);
		this.basePropertyFactory = basePropertyFactory;
	}

	public BasePropertyFactory getBasePropertyFactory() {
		return basePropertyFactory;
	}
}
