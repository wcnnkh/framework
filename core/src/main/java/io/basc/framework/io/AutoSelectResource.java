package io.basc.framework.io;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.event.MultiEventRegistration;
import io.basc.framework.event.Observable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class AutoSelectResource extends ResourceWrapper implements Observable<Resource> {
	private Collection<Resource> resources;

	public AutoSelectResource(Resource... resources) {
		this(Arrays.asList(resources));
	}

	/**
	 * 从多个resource中自动选择一个可用的
	 * 
	 * @param resources 使用优先级从高到低
	 */
	public AutoSelectResource(Collection<Resource> resources) {
		Assert.requiredArgument(!CollectionUtils.isEmpty(resources), "resources");
		this.resources = resources;
	}

	@Override
	public Resource get() {
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
	public EventRegistration registerListener(EventListener<ChangeEvent<Resource>> eventListener) {
		return MultiEventRegistration.registerListener(new EventListener<ChangeEvent<Resource>>() {

			@Override
			public void onEvent(ChangeEvent<Resource> event) {
				eventListener.onEvent(new ChangeEvent<Resource>(event.getEventType(), AutoSelectResource.this));
			}
		}, resources);
	}
}