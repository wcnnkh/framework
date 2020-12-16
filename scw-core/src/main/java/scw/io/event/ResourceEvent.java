package scw.io.event;

import scw.event.ChangeEvent;
import scw.event.EventType;
import scw.io.Resource;

@SuppressWarnings("serial")
public class ResourceEvent extends ChangeEvent<Resource> {

	public ResourceEvent(EventType eventType, Resource resource) {
		super(eventType, resource);
	}

	public ResourceEvent(ChangeEvent<Resource> event) {
		super(event);
	}
}