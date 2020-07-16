package scw.io.event;

import scw.event.support.BasicEvent;
import scw.event.support.EventType;
import scw.io.Resource;

public class ResourceEvent extends BasicEvent {
	private final Resource resource;
	private final long lastModified;

	public ResourceEvent(EventType eventType, Resource resource, long lastModified) {
		super(eventType);
		this.resource = resource;
		this.lastModified = lastModified;
	}

	public ResourceEvent(ResourceEvent resourceEvent) {
		super(resourceEvent);
		this.resource = resourceEvent.resource;
		this.lastModified = resourceEvent.lastModified;
	}

	public Resource getResource() {
		return resource;
	}

	public long getLastModified() {
		return lastModified;
	}

	@Override
	public String toString() {
		return "createTime=" + getCreateTime() + ", lastModified=" + lastModified + ", resource=[" + resource + "]";
	}
}