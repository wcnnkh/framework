package scw.io.event;

import scw.event.support.BasicEvent;
import scw.io.Resource;

public class ResourceEvent extends BasicEvent {
	private final Resource resource;
	private final long lastModified;

	public ResourceEvent(Resource resource, long lastModified) {
		this.resource = resource;
		this.lastModified = lastModified;
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