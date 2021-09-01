package io.basc.framework.io.event;

import io.basc.framework.convert.Converter;
import io.basc.framework.event.AbstractObservable;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.io.Resource;

public class ObservableResource<T> extends AbstractObservable<T> implements AutoCloseable {
	private final Resource resource;
	private final Converter<Resource, T> converter;
	private final EventRegistration eventRegistration;

	public ObservableResource(Resource resource,
			Converter<Resource, T> converter) {
		this.resource = resource;
		this.converter = converter;
		this.eventRegistration = resource.registerListener(new EventListener<ChangeEvent<Resource>>() {
			
			@Override
			public void onEvent(ChangeEvent<Resource> event) {
				publishEvent(new ChangeEvent<T>(event.getEventType(), forceGet()));
			}
		});
	}

	public Resource getResource() {
		return resource;
	}

	public T forceGet() {
		return converter.convert(resource);
	}
	
	@Override
	public void close() {
		eventRegistration.unregister();
	}
}
