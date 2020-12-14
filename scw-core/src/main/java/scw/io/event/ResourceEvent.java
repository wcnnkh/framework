package scw.io.event;

import scw.event.BasicEvent;
import scw.event.EventType;
import scw.io.Resource;

public class ResourceEvent extends BasicEvent {
	private final Resource resource;

	public ResourceEvent(EventType eventType, Resource resource) {
		super(eventType);
		this.resource = resource;
	}

	public ResourceEvent(ResourceEvent resourceEvent) {
		super(resourceEvent);
		this.resource = resourceEvent.resource;
	}

	public Resource getResource() {
		return resource;
	}
}