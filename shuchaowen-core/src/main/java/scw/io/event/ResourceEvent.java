package scw.io.event;

import scw.event.support.BasicEvent;
import scw.event.support.EventType;
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

	@Override
	public String toString() {
		return "eventType=[" + getEventType() + "], createTime=" + getCreateTime() + ", resource=[" + resource + "]";
	}
}