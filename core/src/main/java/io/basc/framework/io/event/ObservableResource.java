package io.basc.framework.io.event;

import java.util.function.Function;

import io.basc.framework.event.AbstractObservable;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.io.Resource;

public class ObservableResource<T> extends AbstractObservable<T> implements AutoCloseable {
	private final Resource resource;
	private final Function<Resource, ? extends T> processor;
	private final EventRegistration eventRegistration;

	public ObservableResource(Resource resource, Function<Resource, ? extends T> processor) {
		this.resource = resource;
		this.processor = processor;
		this.eventRegistration = resource
				.registerListener((event) -> publishEvent(new ChangeEvent<T>(event.getEventType(), forceGet())));
	}

	public Resource getResource() {
		return resource;
	}

	public T forceGet() {
		return processor.apply(resource);
	}

	@Override
	public void close() {
		eventRegistration.unregister();
	}
}
