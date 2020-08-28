package scw.io.event;

import scw.event.support.BasicTypeEvent;
import scw.event.support.EventType;
import scw.io.Resource;

public class ResourceEvent extends BasicTypeEvent {
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

	@Override
	public String toString() {
		return super.toString() + ", resource=[" + resource + "]";
	}
}