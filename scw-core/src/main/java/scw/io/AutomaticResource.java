package scw.io;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import scw.core.Assert;
import scw.core.utils.CollectionUtils;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.MultiEventRegistration;
import scw.io.event.ResourceEvent;

public class AutomaticResource extends ResourceWrapper {
	private Collection<Resource> resources;
	private volatile Resource currentResource;
	
	public AutomaticResource(Resource ...resources) {
		this(Arrays.asList(resources));
	}

	/**
	 * 从多个resource中自动选择一个可用的
	 * @param resources 使用优先级从高到低
	 */
	public AutomaticResource(Collection<Resource> resources) {
		Assert.requiredArgument(!CollectionUtils.isEmpty(resources), "resources");
		this.resources = resources;
		this.currentResource = getCurrentResource();
	}

	private Resource getCurrentResource() {
		Iterator<Resource> iterator = this.resources.iterator();
		while (iterator.hasNext()) {
			Resource resource = iterator.next();
			if (resource.exists() || !iterator.hasNext()) {
				return resource;
			}
		}
		throw new RuntimeException("It's impossible to be here");
	}

	@Override
	public Resource getResource() {
		return currentResource;
	}
	
	@Override
	public EventRegistration registerListener(EventListener<ResourceEvent> eventListener) {
		return MultiEventRegistration.registerListener(new EventListener<ResourceEvent>() {

			@Override
			public void onEvent(ResourceEvent event) {
				eventListener.onEvent(new ResourceEvent(event.getEventType(), AutomaticResource.this));
			}
		}, resources);
	}
}
