package scw.registry;

import scw.event.support.BasicTypeEvent;
import scw.event.support.EventType;

public class RegistryEvent extends BasicTypeEvent {
	private final InstanceInfo instanceInfo;

	public RegistryEvent(EventType eventType, InstanceInfo instanceInfo) {
		super(eventType);
		this.instanceInfo = instanceInfo;
	}

	public InstanceInfo getInstanceInfo() {
		return instanceInfo;
	}
}
